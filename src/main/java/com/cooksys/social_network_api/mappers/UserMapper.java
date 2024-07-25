package com.cooksys.social_network_api.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cooksys.social_network_api.dtos.UserResponseDto;
import com.cooksys.social_network_api.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "username", source = "credentials.username")
    UserResponseDto entityToDto(User entity);

    default List<UserResponseDto> entitiesToDtos(List<User> entities) {
        return entities.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
