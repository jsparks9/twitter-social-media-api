package com.cooksys.social_network_api.services.impl;

import com.cooksys.social_network_api.services.TweetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class TweetServiceImplTest {

    final TweetService tweetService;

    @Autowired
    TweetServiceImplTest(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @Test
    void findMentionsAndTags() {
        String content = "Hey @John and @12Jane, check out #Java! #123";
        List<String> mentions = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        List<String> expectedMentions = List.of("John", "12Jane");
        List<String> expectedTags = List.of("Java", "123");

        tweetService.findMentionsAndTags(content, mentions, tags);

        Assertions.assertEquals(expectedMentions, mentions);
        Assertions.assertEquals(expectedTags, tags);

    }
}