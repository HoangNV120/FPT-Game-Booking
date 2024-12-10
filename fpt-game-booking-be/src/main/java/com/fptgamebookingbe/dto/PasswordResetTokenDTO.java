package com.fptgamebookingbe.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetTokenDTO {

    private Long id;

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotBlank(message = "Token is required.")
    @Size(max = 255, message = "Token must be less than 255 characters.")
    private String token;

    private boolean isUsed = false;
}
