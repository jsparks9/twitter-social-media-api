package com.cooksys.social_network_api.controllers;

import com.cooksys.social_network_api.dtos.*;
import com.cooksys.social_network_api.services.HashtagService;
import com.cooksys.social_network_api.services.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    public final TweetService tweetService;
    public final HashtagService hashtagService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getAllTweets(){
        return tweetService.getAllTweets();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TweetResponseDto getTweet(
            @PathVariable("id") Long id
    ) {
        return tweetService.getTweet(id);
    }

    @GetMapping("/{id}/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getTweetLikes(
            @PathVariable("id") Long id
    ) {
        return tweetService.getTweetLikes(id);
    }

    @GetMapping("/{id}/context")
    @ResponseStatus(HttpStatus.OK)
    public ContextResponseDto getTweetContext(
            @PathVariable("id") Long id
    ) {
        return tweetService.getTweetContext(id);
    }

    @GetMapping("/{id}/mentions")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getTweetMentions(
            @PathVariable("id") Long id
    ) {
        return tweetService.getTweetMentions(id);
    }

    @GetMapping("/{id}/tags")
    @ResponseStatus(HttpStatus.OK)
    public List<HashtagResponseDto> getAllTagsForTweet(
            @PathVariable("id") Long id
    ) {
        return hashtagService.findHashtagsByTweetId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto postTweet(
            @RequestBody @Valid TweetRequestDto tweetRequestDto
    ) {
        return tweetService.postTweet(tweetRequestDto);
    }

    @PostMapping(path = "/{id}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto replyToTweet(
            @RequestBody @Valid TweetRequestDto tweetRequestDto,
            @PathVariable("id") Long id
    ) {
        return tweetService.replyToTweet(id, tweetRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TweetResponseDto deleteTweet(
            @RequestBody @Valid CredentialsDto credentialsDto,
            @PathVariable("id") Long id
    ) {
        return tweetService.deleteTweet(credentialsDto, id);
    }

    @PostMapping("/{id}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLike(
            @RequestBody @Valid CredentialsDto credentialsDto,
            @PathVariable("id") Long id
    ) {
        tweetService.createLike(credentialsDto, id);
    }

    @GetMapping("/{id}/reposts")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getReposts(
            @PathVariable("id") Long id
    ) {
        return tweetService.getReposts(id);
    }

    @PostMapping("/{id}/repost")
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto repostTweet(
            @RequestBody @Valid CredentialsDto credentialsDto,
            @PathVariable("id") Long id
    ) {
        return tweetService.repostTweet(credentialsDto, id);
    }
    
    @GetMapping("/{id}/replies")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getRepliesById(
            @PathVariable("id") Long id
    ) {
    	return tweetService.getRepliesByID(id);
    }

}
