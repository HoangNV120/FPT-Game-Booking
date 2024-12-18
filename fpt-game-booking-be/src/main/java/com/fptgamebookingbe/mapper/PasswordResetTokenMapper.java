package com.fptgamebookingbe.mapper;

import com.fptgamebookingbe.dto.PasswordResetTokenDTO;
import com.fptgamebookingbe.entity.PasswordResetToken;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PasswordResetTokenMapper {

    PasswordResetTokenMapper INSTANCE = Mappers.getMapper(PasswordResetTokenMapper.class);

    PasswordResetTokenDTO passwordResetTokenToPasswordResetTokenDTO(PasswordResetToken passwordResetToken);

    PasswordResetToken passwordResetTokenDTOToPasswordResetToken(PasswordResetTokenDTO passwordResetTokenDTO);
}

