package com.fptgamebookingbe.controller;

import com.fptgamebookingbe.dto.UserChangeInfoDTO;
import com.fptgamebookingbe.service.impl.UserChangeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class UserController {

  private UserChangeImpl userService;

  @Autowired
  public UserController(UserChangeImpl userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  UserChangeInfoDTO getUser(@PathVariable long id)
  {
    return userService.getUserById(id);
  }
}
