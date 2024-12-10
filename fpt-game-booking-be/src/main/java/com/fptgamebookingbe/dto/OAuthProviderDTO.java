package com.fptgamebookingbe.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthProviderDTO {

    private Long id;

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotBlank(message = "Provider is required.")
    @Size(max = 50, message = "Provider must be less than 50 characters.")
    private String provider;

    @NotBlank(message = "Provider User ID is required.")
    @Size(max = 255, message = "Provider User ID must be less than 255 characters.")
    private String providerUserId;

    private String accessToken;
    private String refreshToken;

    private LocalDateTime expiresAt;
}
