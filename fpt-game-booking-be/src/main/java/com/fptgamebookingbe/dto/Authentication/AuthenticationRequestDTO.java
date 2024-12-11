package com.fptgamebookingbe.dto.Authentication;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequestDTO {
    private String email;
    private String password;
}
