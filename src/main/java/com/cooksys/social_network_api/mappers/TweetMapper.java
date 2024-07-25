package com.cooksys.social_network_api.mappers;

import com.cooksys.social_network_api.dtos.TweetRequestDto;
import com.cooksys.social_network_api.dtos.TweetResponseDto;
import com.cooksys.social_network_api.entities.Tweet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TweetMapper {

    TweetResponseDto entityToDto(Tweet tweet);

    List<TweetResponseDto> entitiesToDtos(List<Tweet> tweets);

    Tweet dtoToEntity(TweetRequestDto tweetRequestDto);
}
