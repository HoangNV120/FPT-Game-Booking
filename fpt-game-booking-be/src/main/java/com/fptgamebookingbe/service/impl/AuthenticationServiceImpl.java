package com.fptgamebookingbe.service.impl;

import com.fptgamebookingbe.dto.Authentication.AuthenticationRequestDTO;
import com.fptgamebookingbe.dto.Authentication.AuthenticationResponseDTO;
import com.fptgamebookingbe.dto.InvalidatedTokenDTO;
import com.fptgamebookingbe.entity.InvalidatedToken;
import com.fptgamebookingbe.entity.User;
import com.fptgamebookingbe.exception.AppException;
import com.fptgamebookingbe.exception.ErrorCode;
import com.fptgamebookingbe.mapper.InvalidatedTokenMapper;
import com.fptgamebookingbe.repository.InvalidatedTokenRepository;
import com.fptgamebookingbe.repository.UserRepository;
import com.fptgamebookingbe.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    private final InvalidatedTokenMapper invalidatedTokenMapper;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final UserRepository userRepository;

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponseDTO.builder().token(token).authenticated(true).build();
    }
    @Override
    public String generateToken(User user) {
        ;
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("fptgamebooking")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", "ROLE_" + user.getRole())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean introspectToken(String token) throws JOSEException, ParseException {
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (ParseException | JOSEException |AppException e) {
            isValid = false;
        }
        return isValid;
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
    @Override
    public void logout(String token) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(token, true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedTokenDTO invalidatedTokenDTO =
                    InvalidatedTokenDTO.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(toEntity(invalidatedTokenDTO));
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }
    @Override
    public AuthenticationResponseDTO refreshToken(String token) throws ParseException, JOSEException {
        var signedJWT = verifyToken(token, true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder()
                        .id(jit)
                        .expiryTime(expiryTime)
                        .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var email = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var newToken = generateToken(user);

        return AuthenticationResponseDTO.builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }
    private InvalidatedToken toEntity(InvalidatedTokenDTO dto) {
        return invalidatedTokenMapper.toEntity(dto);
    }
    private InvalidatedTokenDTO toDTO(InvalidatedToken entity) {
        return invalidatedTokenMapper.toDTO(entity);
    }

}
