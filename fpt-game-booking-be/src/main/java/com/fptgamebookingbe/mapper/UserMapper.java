package com.fptgamebookingbe.mapper;

import com.fptgamebookingbe.dto.UserChangeInfoDTO;
import com.fptgamebookingbe.dto.UserDTO;
import com.fptgamebookingbe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);

    UserChangeInfoDTO userToUserChangeInfoDTO(User user);
    UserChangeInfoDTO updateUserChangeInfoDTO(@MappingTarget UserChangeInfoDTO userChangeInfoDTO, UserChangeInfoDTO request);
    User changeInfoDTOToUser(UserChangeInfoDTO userChangeInfoDTO);
}

