package com.fptgamebookingbe.mapper;

import com.fptgamebookingbe.dto.UserDTO;
import com.fptgamebookingbe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
}

