package com.cooksys.social_network_api.mappers;

import org.mapstruct.Mapper;

import com.cooksys.social_network_api.dtos.CredentialsDto;
import com.cooksys.social_network_api.entities.Credentials;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
	
    CredentialsDto entityToDto(Credentials credentials);

    Credentials credentialsDtoToEntity(CredentialsDto credentialsDto);

}
