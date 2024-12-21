package com.fptgamebookingbe.service.impl;

import com.fptgamebookingbe.dto.Response.ApiResponse;
import com.fptgamebookingbe.dto.UserChangeInfoDTO;
import com.fptgamebookingbe.entity.PendingUser;
import com.fptgamebookingbe.entity.User;
import com.fptgamebookingbe.exception.AppException;
import com.fptgamebookingbe.exception.ErrorCode;
import com.fptgamebookingbe.mapper.UserMapper;
import com.fptgamebookingbe.repository.UserRepository;
import com.fptgamebookingbe.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserChangeImpl {

  UserRepository userRepository;
  UserMapper userMapper;
  JavaMailSender mailSender;
  RedisTemplate<String, Object> redisTemplate;

  public void savePendingUser(String token, PendingUser user) {
    redisTemplate.opsForValue().set(token, user, Duration.ofHours(1));
  }

  public User findUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_INVALID));
  }
  public UserChangeInfoDTO getUserById(long id) {
    if(id < 0){
      throw new AppException(ErrorCode.USER_INVALID);
    }
    User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    return userMapper.userToUserChangeInfoDTO(user);
  }

   public UserChangeInfoDTO updateUser(long id,UserChangeInfoDTO user) {
    User user1 = userRepository.findById(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
     PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
     user.setPassword(passwordEncoder.encode(user.getPassword()));
    userMapper.updateUser(user1, user);
    return userMapper.userToUserChangeInfoDTO(userRepository.save(user1));//
  }

  public User addUser(User user) {
    User u = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_INVALID));
    if(u != null){
      throw new AppException(ErrorCode.USER_EXISTED);
    }
    return userRepository.save(user);
  }

    public void sendActivationEmail(String email, String activationLink) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setTo(email);
    helper.setSubject("Account Activation");
    helper.setText("<p>Please click the link below to activate your account:</p>"
        + "<a href='" + activationLink + "'>Activate Account</a>", true);

    mailSender.send(message);
  }
}
