package com.fptgamebookingbe.service;

import com.fptgamebookingbe.dto.UserDTO;
import com.fptgamebookingbe.entity.User;

public interface UserService {
    /**
     * Retrieves the user with the specified email address.
     *
     * @param email the email address of the user to be retrieved
     * @return the user with the specified email address
     */
    UserDTO getUserByEmail(String email);


    /**
     * Creates a password reset token for the given user with the specified token.
     *
     * @param user the account for which the password reset token is being created
     */
    String createPasswordResetTokenForUser(User user);

    /**
     * Validates the password reset token.
     *
     * @param token the token to be validated
     * @return true if the token is valid, false otherwise
     */
    boolean validatePasswordResetToken(String token);

    /**
     * Updates the password for the specified user using the provided token and new password.
     *
     * @param token    the token associated with the password reset
     * @param password the new password to be set for the account
     */
    void updatePasswordForUser(String token, String password);

    User createUserFromDTO(UserDTO userDTO);

    UserDTO userToUserDTO(User user);

    UserDTO getUserDTO(User user);

}
