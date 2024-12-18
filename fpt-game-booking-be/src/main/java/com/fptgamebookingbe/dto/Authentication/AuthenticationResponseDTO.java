package com.fptgamebookingbe.dto.Authentication;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponseDTO {
    private String token;
    private boolean authenticated;
}
