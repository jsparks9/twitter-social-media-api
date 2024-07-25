package com.cooksys.social_network_api.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.social_network_api.dtos.HashtagResponseDto;
import com.cooksys.social_network_api.entities.Hashtag;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

    HashtagResponseDto entityToDto(Hashtag entity);

    List<HashtagResponseDto> entitiesToDtos(List<Hashtag> entities);

}
