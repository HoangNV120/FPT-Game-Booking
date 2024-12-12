package com.fptgamebookingbe.service;

import com.fptgamebookingbe.dto.Authentication.AuthenticationRequestDTO;
import com.fptgamebookingbe.dto.Authentication.AuthenticationResponseDTO;
import com.fptgamebookingbe.entity.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    /**
     * Authenticates the user based on the provided authentication request.
     *
     * @param request the authentication request containing user credentials
     * @return the authentication response containing the authentication token
     */
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);

    /**
     * Generates a token for the specified user.
     *
     * @param user the user for whom the token is to be generated
     * @return the generated token
     */
    String generateToken(User user);

    /**
     * Introspects the provided token to determine its validity.
     *
     * @param token the token to be introspected
     * @return true if the token is valid, false otherwise
     * @throws JOSEException if there is an error processing the token
     * @throws ParseException if there is an error parsing the token
     */
    boolean introspectToken(String token) throws JOSEException, ParseException;

    /**
     * Refreshes the provided token and returns a new authentication response.
     *
     * @param token the token to be refreshed
     * @return the new authentication response containing the refreshed token
     * @throws ParseException if there is an error parsing the token
     * @throws JOSEException if there is an error processing the token
     */
    AuthenticationResponseDTO refreshToken(String token) throws ParseException, JOSEException;

    /**
     * Logs out the user by invalidating the provided token.
     *
     * @param token the token to be invalidated
     * @throws ParseException if there is an error parsing the token
     * @throws JOSEException if there is an error processing the token
     */
    void logout(String token) throws ParseException, JOSEException;

    /**
     * Authenticates the user using an outbound authentication code.
     *
     * @param code the outbound authentication code
     * @return the authentication response containing the authentication token
     */
    AuthenticationResponseDTO outboundAuthenticate(String code);
}