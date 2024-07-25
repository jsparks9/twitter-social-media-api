package com.cooksys.social_network_api.services;

import com.cooksys.social_network_api.dtos.HashtagResponseDto;
import com.cooksys.social_network_api.entities.Hashtag;

import java.util.List;

public interface HashtagService {

    List<HashtagResponseDto> getAllHashtags();

    Hashtag createHashtag(String label);

    List<Hashtag> createHashtags(List<String> labels);

    List<HashtagResponseDto> findHashtagsByTweetId(Long id);
}
