package com.cooksys.social_network_api.mappers;

import org.mapstruct.Mapper;

import com.cooksys.social_network_api.dtos.ProfileDto;
import com.cooksys.social_network_api.entities.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
	
    ProfileDto entityToDto(Profile profile);

    Profile profileDtoToEntity(ProfileDto profileDto);

}
