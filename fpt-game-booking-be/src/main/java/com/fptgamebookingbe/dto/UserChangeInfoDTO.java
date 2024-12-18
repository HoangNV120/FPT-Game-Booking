package com.fptgamebookingbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChangeInfoDTO {
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
}
