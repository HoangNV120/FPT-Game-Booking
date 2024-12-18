package com.fptgamebookingbe.controller;

import com.fptgamebookingbe.dto.Authentication.AuthenticationRequestDTO;
import com.fptgamebookingbe.dto.Authentication.AuthenticationResponseDTO;
import com.fptgamebookingbe.dto.IntrospectDTO;
import com.fptgamebookingbe.dto.LogoutDTO;
import com.fptgamebookingbe.dto.PasswordResetDTO;
import com.fptgamebookingbe.dto.RefreshDTO;
import com.fptgamebookingbe.service.AuthenticationService;
import com.fptgamebookingbe.service.MailService;
import com.fptgamebookingbe.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthenticationController {


    @NonFinal
    @Value("${domain.server}")
    protected String DOMAIN_SERVER;

    private final UserService userService;
    private final MailService mailService;

    private final AuthenticationService authenticationService;

    /**
     * Request to initiate the password reset process.
     * Sends a reset link containing a token to the user's email.
     *
     * @param email the email address of the user
     * @return response indicating whether the email was sent
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            // Check if the user exists
            var userDTO = userService.getUserByEmail(email);
            if (userDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Create password reset token
            var user = userService.createUserFromDTO(userDTO);

            // Check if the user is still valid after mapping
            if (user == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user for password reset");
            }

            String token = userService.createPasswordResetTokenForUser(user);
            String resetLink = DOMAIN_SERVER + "/api/v1/auth/reset-password?token=" + token;

            // Send email
            mailService.sendEmail(email, "Password Reset Request",
                    "Click the following link to reset your password: " + resetLink);

            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email.");
        } catch (Exception e) {
            // Catch any other exceptions to avoid server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Endpoint to reset the user's password using the token.
     *
     * @param resetDTO contains the token and the new password
     * @return response indicating the success or failure of the operation
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO resetDTO) {
        // Validate the token
        boolean isValidToken = userService.validatePasswordResetToken(resetDTO.getToken());
        if (!isValidToken) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }

        // Update password
        userService.updatePasswordForUser(resetDTO.getToken(), resetDTO.getNewPassword());

        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @PostMapping("/token")
    ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO request) {
        var result = authenticationService.authenticate(request);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/introspect")
    ResponseEntity<Boolean> authenticate(@RequestBody IntrospectDTO request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspectToken(request.getToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    ResponseEntity<AuthenticationResponseDTO> refresh(@RequestBody RefreshDTO request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request.getToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@RequestBody LogoutDTO request) throws ParseException, JOSEException {
        authenticationService.logout(request.getToken());
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/outbound/authentication")
    ResponseEntity<AuthenticationResponseDTO> outboundAuthenticate(
            @RequestParam("code") String code
    ){
        var result = authenticationService.outboundAuthenticate(code);
        return ResponseEntity.ok(result);
    }
}
