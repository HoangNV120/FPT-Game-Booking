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
     * Creates a password reset token for the given user.
     *
     * @param user the account for which the password reset token is being created
     * @return the generated password reset token
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

    /**
     * Creates a User entity from a UserDTO.
     *
     * @param userDTO the UserDTO to be converted
     * @return the created User entity
     */
    User createUserFromDTO(UserDTO userDTO);

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to be converted
     * @return the converted UserDTO
     */
    UserDTO userToUserDTO(User user);

    /**
     * Retrieves a UserDTO for the specified User entity.
     *
     * @param user the User entity for which the UserDTO is to be retrieved
     * @return the retrieved UserDTO
     */
    UserDTO getUserDTO(User user);
}