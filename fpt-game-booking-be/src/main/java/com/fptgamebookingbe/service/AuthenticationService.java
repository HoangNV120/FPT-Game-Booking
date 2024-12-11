package com.fptgamebookingbe.service;

import com.fptgamebookingbe.dto.Authentication.AuthenticationRequestDTO;
import com.fptgamebookingbe.dto.Authentication.AuthenticationResponseDTO;
import com.fptgamebookingbe.entity.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
    String generateToken(User user);
    boolean introspectToken(String token) throws JOSEException, ParseException;
    AuthenticationResponseDTO refreshToken(String token) throws ParseException, JOSEException;
    void logout(String token) throws ParseException, JOSEException;
}
