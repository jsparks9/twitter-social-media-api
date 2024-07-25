package com.cooksys.social_network_api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ContextResponseDto {

    private TweetResponseDto target;

    private List<TweetResponseDto> before;

    private List<TweetResponseDto> after;

}