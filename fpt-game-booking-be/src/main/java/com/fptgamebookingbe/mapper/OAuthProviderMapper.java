package com.fptgamebookingbe.mapper;

import com.fptgamebookingbe.dto.OAuthProviderDTO;
import com.fptgamebookingbe.entity.OAuthProvider;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OAuthProviderMapper {

    OAuthProviderMapper INSTANCE = Mappers.getMapper(OAuthProviderMapper.class);

    OAuthProviderDTO oauthProviderToOAuthProviderDTO(OAuthProvider oauthProvider);

    OAuthProvider oauthProviderDTOToOAuthProvider(OAuthProviderDTO oauthProviderDTO);
}

