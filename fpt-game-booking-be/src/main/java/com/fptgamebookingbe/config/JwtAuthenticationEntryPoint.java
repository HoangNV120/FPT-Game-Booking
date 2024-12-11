package com.fptgamebookingbe.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fptgamebookingbe.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        // Create a ResponseEntity object
        ResponseEntity<?> responseEntity = ResponseEntity
                .status(errorCode.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "code", errorCode.getCode(),
                        "message", errorCode.getMessage()
                ));

        // Use ObjectMapper to write the body to the HttpServletResponse
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentType(Objects.requireNonNull(responseEntity.getHeaders().getContentType()).toString());
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
        response.flushBuffer();
    }

}