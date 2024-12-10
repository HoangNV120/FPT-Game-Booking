package com.fptgamebookingbe.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Name is required.")
    @Size(max = 255, message = "Name must be less than 255 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Size(max = 255, message = "Email must be less than 255 characters.")
    private String email;

    @Size(max = 255, message = "Password must be less than 255 characters.")
    private String password;

    private boolean isOauthOnly = false;

    @NotBlank(message = "Role is required.")
    @Pattern(regexp = "user|admin|premium", message = "Invalid role.")
    private String role;

    private boolean isBanned = false;
}

