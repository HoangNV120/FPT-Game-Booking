package com.fptgamebookingbe.controller;

import com.fptgamebookingbe.dto.Response.ApiResponse;
import com.fptgamebookingbe.dto.UserChangeInfoDTO;
import com.fptgamebookingbe.entity.PendingUser;
import com.fptgamebookingbe.entity.User;
import com.fptgamebookingbe.exception.AppException;
import com.fptgamebookingbe.exception.ErrorCode;
import com.fptgamebookingbe.service.impl.UserChangeImpl;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final RedisTemplate redisTemplate;
  private UserChangeImpl userService;

  @Autowired
  public UserController(UserChangeImpl userService,
      @Qualifier("redisTemplate") RedisTemplate redisTemplate) {
    this.userService = userService;
    this.redisTemplate = redisTemplate;
  }

  @GetMapping("/{id}")
  ApiResponse<UserChangeInfoDTO> getUser(@PathVariable long id)
  {
    UserChangeInfoDTO result = userService.getUserById(id);
    return ApiResponse.<UserChangeInfoDTO>builder()
        .result(result)
        .build();
  }

  @PutMapping("/update-info/{id}")
  ApiResponse<UserChangeInfoDTO> updateUserInfor(@PathVariable int id ,@RequestBody UserChangeInfoDTO request)
  {
    return ApiResponse.<UserChangeInfoDTO>builder()
        .result(userService.updateUser(id,request))
        .build();
  }

  @PostMapping("/register")
  public ApiResponse<String> registerUser(PendingUser pendingUser) {
    if (userService.findUserByEmail(pendingUser.getEmail()) != null){
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    String token = UUID.randomUUID().toString();
    userService.savePendingUser(token, pendingUser);

    String activationLink = "http://localhost:8080/api/activate?token=" + token;
    try {
      userService.sendActivationEmail(pendingUser.getEmail(), activationLink);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send activation email");
    }

    return ApiResponse.<String>builder()
        .result("User registered successfully. Please check your email to activate your account.")
        .build();
  }

  @GetMapping("/active")
  public ApiResponse<String> activeAccount(@RequestParam String token){
    if (token == null || token.isEmpty()) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    PendingUser pendingUser = (PendingUser) redisTemplate.opsForValue().get(token);

    if(pendingUser == null)
    {
      throw  new AppException(ErrorCode.USER_NOT_EXISTED);
    }

    User user = new User();
    user.setName(pendingUser.getName());
    user.setEmail(pendingUser.getEmail());
    user.setPassword(new BCryptPasswordEncoder(10).encode(pendingUser.getPassword()));
    user.setBanned(false);
    user.setRole("user");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    userService.addUser(user);
    redisTemplate.delete(token);
    return ApiResponse.<String>builder()
        .result("Account activated successfully.")
        .build();
  }
}
