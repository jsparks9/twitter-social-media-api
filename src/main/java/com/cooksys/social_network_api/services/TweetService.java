package com.cooksys.social_network_api.services;

import com.cooksys.social_network_api.dtos.*;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getAllTweets();

    List<TweetResponseDto> findTweetsByHashtagLabel(String label);

    TweetResponseDto getTweet(Long id);

    List<UserResponseDto> getTweetLikes(Long id);

    ContextResponseDto getTweetContext(Long id);

    TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetPostDto);

    void findMentionsAndTags(String content, List<String> mentions, List<String> tags);

    TweetResponseDto postTweet(TweetRequestDto tweetRequestDto);

    TweetResponseDto deleteTweet(CredentialsDto credentialsDto, Long id);

    void createLike(CredentialsDto credentialsDto, Long id);

    List<TweetResponseDto> getReposts(Long id);

    List<UserResponseDto> getTweetMentions(Long id);

    TweetResponseDto repostTweet(CredentialsDto credentialsDto, Long id);

    List<TweetResponseDto> getRepliesByID(Long id);
}
