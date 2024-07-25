package com.cooksys.social_network_api.dtos;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class TweetResponseDto {

    private Long id;

    private UserResponseDto author;

    private Timestamp posted;

    private String content;

    private TweetResponseDto inReplyTo;

    private TweetResponseDto repostOf;

}
