package com.fptgamebookingbe.service.impl;

import com.fptgamebookingbe.dto.Response.ApiResponse;
import com.fptgamebookingbe.dto.UserChangeInfoDTO;
import com.fptgamebookingbe.entity.User;
import com.fptgamebookingbe.exception.AppException;
import com.fptgamebookingbe.exception.ErrorCode;
import com.fptgamebookingbe.mapper.UserMapper;
import com.fptgamebookingbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserChangeImpl {

  @Autowired
  private  UserRepository userRepository;
  @Autowired
  private UserMapper userMapper;

  public UserChangeInfoDTO getUserById(long id) {
    if(id < 0){
      throw new AppException(ErrorCode.USER_INVALID);
    }
    User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    return userMapper.userToUserChangeInfoDTO(user);
  }

   public UserChangeInfoDTO updateUser(UserChangeInfoDTO user) {
    UserChangeInfoDTO userChangeInfoDTO = userMapper.userToUserChangeInfoDTO(userRepository.findById(
        user.getId()).orElseThrow(() -> new RuntimeException("User not found")));

    userMapper.updateUserChangeInfoDTO(userChangeInfoDTO, user);
    return userMapper.userToUserChangeInfoDTO(userRepository.save(userMapper.changeInfoDTOToUser(userChangeInfoDTO)));
  }

}
