package com.fptgamebookingbe.controller;

import com.fptgamebookingbe.dto.PasswordResetDTO;
import com.fptgamebookingbe.dto.UserDTO;
import com.fptgamebookingbe.entity.User;
import com.fptgamebookingbe.service.MailService;
import com.fptgamebookingbe.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserService userService;
    private final MailService mailService;

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
            String resetLink = "http://localhost:8080/api/v1/auth/reset-password?token=" + token;

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
}
