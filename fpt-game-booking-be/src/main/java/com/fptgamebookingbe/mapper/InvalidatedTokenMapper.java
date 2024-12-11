package com.fptgamebookingbe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.fptgamebookingbe.dto.InvalidatedTokenDTO;
import com.fptgamebookingbe.entity.InvalidatedToken;

@Mapper(componentModel = "spring")
public interface InvalidatedTokenMapper {
    InvalidatedTokenMapper INSTANCE = Mappers.getMapper(InvalidatedTokenMapper.class);

    InvalidatedTokenDTO toDTO(InvalidatedToken entity);

    InvalidatedToken toEntity(InvalidatedTokenDTO dto);
}

