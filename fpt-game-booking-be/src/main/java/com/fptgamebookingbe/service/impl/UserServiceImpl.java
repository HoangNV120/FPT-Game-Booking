package com.fptgamebookingbe.service.impl;

import com.fptgamebookingbe.dto.UserDTO;
import com.fptgamebookingbe.entity.PasswordResetToken;
import com.fptgamebookingbe.entity.User;
import com.fptgamebookingbe.mapper.UserMapper;
import com.fptgamebookingbe.repository.PasswordResetTokenRepository;
import com.fptgamebookingbe.repository.UserRepository;
import com.fptgamebookingbe.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public UserDTO getUserByEmail(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(this::userToUserDTO).orElse(null);
    }

    @Override
    public String createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .build();;
        passwordResetTokenRepository.save(passwordResetToken);
        return token;
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository.findByToken(token);
        if(optionalPasswordResetToken.isEmpty()) {
            return false;
        }
        PasswordResetToken passwordResetToken = optionalPasswordResetToken.get();

        if(LocalDateTime.now().isAfter(passwordResetToken.getCreatedAt().plusDays(1))
        || passwordResetToken.isUsed()) {
            return false;
        }

        passwordResetToken.setUsed(true);
        return true;
    }

    @Override
    public void updatePasswordForUser(String token, String password) {
        Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository.findByToken(token);
        if(optionalPasswordResetToken.isEmpty()) {
            return;
        }
        PasswordResetToken passwordResetToken = optionalPasswordResetToken.get();
        User user = passwordResetToken.getUser();
        user.setPassword(password);
        userRepository.save(user);
    }

    @Override
    public User createUserFromDTO(UserDTO userDTO) {
        return userMapper.userDTOToUser(userDTO);
    }

    @Override
    public UserDTO userToUserDTO(User user) {
        return userMapper.userToUserDTO(user);
    }

    @Override
    public UserDTO getUserDTO(User user) {
        return null;
    }

}
