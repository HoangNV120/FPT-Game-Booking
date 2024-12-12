package com.fptgamebookingbe.service.impl;

import com.fptgamebookingbe.dto.Authentication.AuthenticationRequestDTO;
import com.fptgamebookingbe.dto.Authentication.AuthenticationResponseDTO;
import com.fptgamebookingbe.dto.ExchangeToken.ExchangeTokenRequestDTO;
import com.fptgamebookingbe.dto.InvalidatedTokenDTO;
import com.fptgamebookingbe.entity.InvalidatedToken;
import com.fptgamebookingbe.entity.User;
import com.fptgamebookingbe.exception.AppException;
import com.fptgamebookingbe.exception.ErrorCode;
import com.fptgamebookingbe.mapper.InvalidatedTokenMapper;
import com.fptgamebookingbe.repository.InvalidatedTokenRepository;
import com.fptgamebookingbe.repository.httpclient.OutboundIdentityClient;
import com.fptgamebookingbe.repository.UserRepository;
import com.fptgamebookingbe.repository.httpclient.OutboundUserClient;
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
    // JWT signer key, read from application properties
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    // Token validity duration in seconds, read from application properties
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    // Token refreshable duration, allowing refresh within this time frame
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    // Configuration for outbound identity client
    @NonFinal
    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code"; // OAuth2 grant type

    // Repositories and clients for accessing data and external services
    private final InvalidatedTokenMapper invalidatedTokenMapper;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final UserRepository userRepository;
    private final OutboundIdentityClient outboundIdentityClient;
    private final OutboundUserClient outboundUserClient;

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        // Password encoder for verifying user credentials
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // Find user by email or throw exception if not found
        var user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Validate the password
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Generate JWT token for authenticated user
        var token = generateToken(user);

        return AuthenticationResponseDTO.builder().token(token).authenticated(true).build();
    }

    @Override
    public String generateToken(User user) {
        // Create JWT header with algorithm
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Build JWT claims (payload)
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail()) // Email as the subject
                .issuer("fptgamebooking") // Token issuer
                .issueTime(new Date()) // Issue time
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli())) // Expiration time
                .jwtID(UUID.randomUUID().toString()) // Unique token ID
                .claim("scope", "ROLE_" + user.getRole()) // User role as a claim
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            // Sign the token with the secret key
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize(); // Return the token as a string
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthenticationResponseDTO outboundAuthenticate(String code) {
        // Exchange authorization code for access token using the identity client
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequestDTO.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        log.info("TOKEN RESPONSE {}", response);

        // Fetch user information using the access token
        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        log.info("User Info {}", userInfo);

        // Create a new user if it doesn't already exist
        var user = userRepository.findByEmail(userInfo.getEmail()).orElseGet(
                () -> userRepository.save(User.builder()
                        .email(userInfo.getEmail())
                        .name(userInfo.getGivenName() + " " + userInfo.getFamilyName())
                        .role("user")
                        .build()));

        // Generate a token for the user
        var token = generateToken(user);

        // Return the authentication response
        return AuthenticationResponseDTO.builder()
                .token(token)
                .build();
    }

    @Override
    public boolean introspectToken(String token) throws JOSEException, ParseException {
        // Validate token's authenticity and expiration
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (ParseException | JOSEException | AppException e) {
            isValid = false;
        }
        return isValid;
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        // Verify the token signature
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        // Calculate token's expiry based on refresh logic
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

        // Check if the token is already invalidated
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    @Override
    public void logout(String token) throws ParseException, JOSEException {
        try {
            // Verify the token and add it to the invalidated list
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

        // Mark the old token as invalid
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder()
                        .id(jit)
                        .expiryTime(expiryTime)
                        .build();

        invalidatedTokenRepository.save(invalidatedToken);

        // Generate a new token for the user
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
