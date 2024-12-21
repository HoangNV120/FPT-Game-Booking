package com.fptgamebookingbe.controller;

import com.fptgamebookingbe.dto.Response.ApiResponse;
import com.fptgamebookingbe.dto.UserChangeInfoDTO;
import com.fptgamebookingbe.service.impl.UserChangeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private UserChangeImpl userService;

  @Autowired
  public UserController(UserChangeImpl userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  ApiResponse<UserChangeInfoDTO> getUser(@PathVariable long id)
  {
    UserChangeInfoDTO result = userService.getUserById(id);
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setResult(result);
    return apiResponse;
  }

  @PutMapping("/update-info/{id}")
  ApiResponse<UserChangeInfoDTO> updateUserInfor(@PathVariable int id ,@RequestBody UserChangeInfoDTO request)
  {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setResult(userService.updateUser(id,request));
    return apiResponse;
  }
}
