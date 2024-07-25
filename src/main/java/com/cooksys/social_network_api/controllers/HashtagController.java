package com.cooksys.social_network_api.controllers;

import com.cooksys.social_network_api.dtos.HashtagResponseDto;
import com.cooksys.social_network_api.dtos.TweetResponseDto;
import com.cooksys.social_network_api.services.HashtagService;

import com.cooksys.social_network_api.services.TweetService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class HashtagController {

    private final HashtagService hashtagService;
    private final TweetService tweetService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<HashtagResponseDto> getAllHashtags() {
        return hashtagService.getAllHashtags();
    }

    @GetMapping("/{label}")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getAllTweetsWithLabel(
            @PathVariable("label") String label
    ) {
        return tweetService.findTweetsByHashtagLabel(label);
    }

}
