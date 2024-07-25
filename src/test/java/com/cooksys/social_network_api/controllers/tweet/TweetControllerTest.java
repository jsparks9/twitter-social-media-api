package com.cooksys.social_network_api.controllers.tweet;

import com.cooksys.social_network_api.dtos.CredentialsDto;
import com.cooksys.social_network_api.dtos.TweetRequestDto;
import com.cooksys.social_network_api.dtos.TweetResponseDto;
import com.cooksys.social_network_api.entities.Credentials;
import com.cooksys.social_network_api.entities.Hashtag;
import com.cooksys.social_network_api.entities.Tweet;
import com.cooksys.social_network_api.entities.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;

import static com.cooksys.social_network_api.controllers.user.UserControllerTest.assertTimeEquals;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TweetControllerTest {
    private final String CONTENT_TYPE = "application/json";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    TweetControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @PersistenceContext
    private EntityManager entityManager;

    private <T> T getRandom(Class<T> clazz, boolean useDeleteCondition) {
        String tableName = clazz.getSimpleName();
        // Java 15 text blocks feature
        final String retrieveRandomQuery = String.format(
                """
                SELECT t FROM %s t
                %s
                ORDER BY FUNCTION('RAND')
                LIMIT 1
                """, tableName, (useDeleteCondition ? "WHERE t.deleted = false" : ""));
        return entityManager.createQuery(retrieveRandomQuery, clazz)
                .getSingleResult();
    }
    private <T> T getRandom(Class<T> clazz) {
        return getRandom(clazz, true);
    }

    @Test
    void getAllTweets() throws Exception {
        final String HTTP_URI = "/tweets";

        final String retrieveTweetQuery = "SELECT t FROM Tweet t";
        List<Tweet> databaseTweets = entityManager.createQuery(retrieveTweetQuery, Tweet.class).getResultList();
        databaseTweets.sort(Comparator.comparing(Tweet::getPosted));
        List<Tweet> nonDeletedDbTweets = new ArrayList<>(databaseTweets);
        List<Tweet> deletedTweets = new ArrayList<>(databaseTweets);
        nonDeletedDbTweets.removeIf(Tweet::getDeleted);
        deletedTweets.removeIf(t -> !t.getDeleted());
        List<Long> deletedTweetIds = deletedTweets.stream().map(Tweet::getId).toList();

        String resp = mockMvc.perform(get(HTTP_URI))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<TweetResponseDto> tweetRespList = objectMapper.readValue(resp, new TypeReference<>() {});
        tweetRespList.sort(Comparator.comparing(TweetResponseDto::getPosted));

        Assertions.assertEquals(nonDeletedDbTweets.size(), tweetRespList.size());
        for (int i = 0; i < tweetRespList.size(); i++)
            compareTweetAndTweetDto(nonDeletedDbTweets.get(i), tweetRespList.get(i));
        for (long id: tweetRespList.stream().map(TweetResponseDto::getId).toList())
            Assertions.assertFalse(deletedTweetIds.contains(id));

    }

    public void compareAuthoringUserAndTweetDto(User user, TweetResponseDto dto) {

        // Compare First, Last, Email, and Phone in the database-retrieved user and tweetResponseDto user
        Assertions.assertEquals(user.getProfile().getFirstName(), dto.getAuthor().getProfile().getFirstName());
        Assertions.assertEquals(user.getProfile().getLastName(), dto.getAuthor().getProfile().getLastName());
        Assertions.assertEquals(user.getProfile().getEmail(), dto.getAuthor().getProfile().getEmail());
        Assertions.assertEquals(user.getProfile().getPhone(), dto.getAuthor().getProfile().getPhone());

        // Compare username and joined (time of user creation)
        Assertions.assertEquals(user.getCredentials().getUsername(), dto.getAuthor().getUsername());
        assertTimeEquals(user.getJoined(), dto.getAuthor().getJoined());
    }

    public void compareTweetAndTweetDto(Tweet tweet, TweetResponseDto dto) {
        Assertions.assertEquals(tweet.getId(), dto.getId());
        assertTimeEquals(tweet.getPosted(), dto.getPosted());
        Assertions.assertEquals(tweet.getContent(), dto.getContent());
        if (tweet.getRepostOf() == null || dto.getRepostOf() == null) {
            Assertions.assertNull(tweet.getRepostOf());
            Assertions.assertNull(dto.getRepostOf());
        } else {
            compareTweetAndTweetDto(tweet.getRepostOf(), dto.getRepostOf());
        }
        if (tweet.getInReplyTo() == null || dto.getInReplyTo() == null) {
            Assertions.assertNull(tweet.getInReplyTo());
            Assertions.assertNull(dto.getInReplyTo());
        } else {
            compareTweetAndTweetDto(tweet.getInReplyTo(), dto.getInReplyTo());
        }
        compareAuthoringUserAndTweetDto(tweet.getAuthor(), dto);
    }

    @Test
    void getTweet() throws Exception {
        final String HTTP_URI = "/tweets/{id}";
        Tweet randomTweet = getRandom(Tweet.class);
        String resp = mockMvc.perform(get(HTTP_URI, randomTweet.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        TweetResponseDto respDto = objectMapper.readValue(resp, TweetResponseDto.class);
        compareTweetAndTweetDto(randomTweet, respDto);
    }

    @Test
    void getTweetLikes() throws Exception {
        final String HTTP_URI = "/tweets/{id}/likes";
        Tweet randomTweet = getRandom(Tweet.class);
        String resp = mockMvc.perform(get(HTTP_URI, randomTweet.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // TODO : finish this
    }

    @Test
    @Transactional  // Prevents Hibernate Table entities from disappearing after one .get() call
    void postTweet() throws Exception {
        final String HTTP_URI = "/tweets";
        final User randomUserMention1 = getRandom(User.class);
        final User randomUserMention2 = getRandom(User.class);
        List<User> expectedMentions = new ArrayList<>(Arrays.asList(randomUserMention1, randomUserMention2));
        final User userPostingTweet = getRandom(User.class);
        final String hashtagFromDb = getRandom(Hashtag.class, false).getLabel();
        final String additionalHashtag = UUID.randomUUID().toString().replaceAll("-","");
        Assertions.assertNotEquals(hashtagFromDb, additionalHashtag);
        final String newTweetContent = UUID.randomUUID() +
                "@" + randomUserMention1.getCredentials().getUsername() + "," +
                UUID.randomUUID() + " #" + hashtagFromDb + " " +
                "@" + randomUserMention2.getCredentials().getUsername() + " " +
                UUID.randomUUID() + " #" + additionalHashtag;
        TweetRequestDto tweetRequestDto = new TweetRequestDto();
        tweetRequestDto.setContent(newTweetContent);
        Credentials creds = userPostingTweet.getCredentials();
        CredentialsDto credentialsDto = new CredentialsDto();
        credentialsDto.setUsername(creds.getUsername());
        credentialsDto.setPassword(creds.getPassword());
        tweetRequestDto.setCredentials(credentialsDto);

        final String request = objectMapper.writeValueAsString(tweetRequestDto);
        String resp = mockMvc.perform(post(HTTP_URI)
                        .contentType(CONTENT_TYPE)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();



        final String retrieveTweetQuery =
                "SELECT t FROM Tweet t JOIN FETCH t.author WHERE t.content = '" + newTweetContent + "'";
        List<Tweet> createdTweets = entityManager.createQuery(retrieveTweetQuery, Tweet.class).getResultList();
        Assertions.assertEquals(1, createdTweets.size());
        Tweet createdTweet = createdTweets.get(0);
        Assertions.assertEquals(userPostingTweet, createdTweet.getAuthor());
        Assertions.assertEquals(newTweetContent, createdTweet.getContent());
        Assertions.assertNull(createdTweet.getRepostOf());
        Assertions.assertNull(createdTweet.getInReplyTo());

        TweetResponseDto tweetResponseDto = objectMapper.readValue(resp, TweetResponseDto.class);

        compareAuthoringUserAndTweetDto(userPostingTweet, tweetResponseDto);
        compareTweetAndTweetDto(createdTweet, tweetResponseDto);

        Assertions.assertEquals(userPostingTweet, createdTweet.getAuthor());

        Assertions.assertNull(tweetResponseDto.getRepostOf());
        Assertions.assertNull(tweetResponseDto.getInReplyTo());

        // Test @Mentions
        Set<User> expectedMentionsSet = new HashSet<>(expectedMentions);
        Set<User> actualMentionsSet = new HashSet<>(createdTweet.getUserMentions());
        Assertions.assertEquals(expectedMentionsSet.size(), actualMentionsSet.size());
        Assertions.assertEquals(expectedMentionsSet, actualMentionsSet);

        Set<String> expectedTags = new HashSet<>(Set.of(hashtagFromDb, additionalHashtag));
        Assertions.assertEquals(expectedTags.size(), createdTweet.getHashtags().size());
        for (Hashtag h: createdTweet.getHashtags()) {
            Assertions.assertTrue(expectedTags.contains(h.getLabel()));
        }

    }

    @Test
    @Transactional  // Prevents Hibernate Table entities from disappearing after one .get() call
    void replyToTweet() throws Exception {
        final String HTTP_URI = "/tweets/{id}/reply";
        final Tweet randomTweetBeingRepliedTo = getRandom(Tweet.class);
        final User randomUserMention1 = getRandom(User.class);
        final User randomUserMention2 = getRandom(User.class);
        List<User> expectedMentions = new ArrayList<>(Arrays.asList(randomUserMention1, randomUserMention2));
        final Long id = randomTweetBeingRepliedTo.getId();
        final User userPostingTweet = getRandom(User.class);
        final String hashtagFromDb = getRandom(Hashtag.class, false).getLabel();
        final String additionalHashtag = UUID.randomUUID().toString().replaceAll("-","");
        Assertions.assertNotEquals(hashtagFromDb, additionalHashtag);
        final String newTweetContent = UUID.randomUUID() +
                "@" + randomUserMention1.getCredentials().getUsername() + "," +
                UUID.randomUUID() + " #" + hashtagFromDb + " " +
                "@" + randomUserMention2.getCredentials().getUsername() + " " +
                UUID.randomUUID() + " #" + additionalHashtag;
        TweetRequestDto tweetRequestDto = new TweetRequestDto();
        tweetRequestDto.setContent(newTweetContent);
        Credentials creds = userPostingTweet.getCredentials();
        CredentialsDto credentialsDto = new CredentialsDto();
        credentialsDto.setUsername(creds.getUsername());
        credentialsDto.setPassword(creds.getPassword());
        tweetRequestDto.setCredentials(credentialsDto);

        final String request = objectMapper.writeValueAsString(tweetRequestDto);
        String resp = mockMvc.perform(post(HTTP_URI, id)
                                .contentType(CONTENT_TYPE)
                                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        final String retrieveTweetQuery =
                "SELECT t FROM Tweet t JOIN FETCH t.author WHERE t.content = '" + newTweetContent + "'";
        List<Tweet> createdTweets = entityManager.createQuery(retrieveTweetQuery, Tweet.class).getResultList();
        Assertions.assertEquals(1, createdTweets.size());
        Tweet createdTweet = createdTweets.get(0);

        Assertions.assertEquals(userPostingTweet, createdTweet.getAuthor());
        Assertions.assertEquals(newTweetContent, createdTweet.getContent());
        Assertions.assertNull(createdTweet.getRepostOf());
        // Null because "A reply has content and inReplyTo values, but no repostOf value" - ReadMe Doc

        TweetResponseDto tweetResponseDto = objectMapper.readValue(resp, TweetResponseDto.class);

        compareAuthoringUserAndTweetDto(userPostingTweet, tweetResponseDto);
        compareTweetAndTweetDto(createdTweet, tweetResponseDto);

        Assertions.assertNull(tweetResponseDto.getRepostOf());
        Assertions.assertEquals(randomTweetBeingRepliedTo.getId(), tweetResponseDto.getInReplyTo().getId());

        // Test @Mentions
        Set<User> expectedMentionsSet = new HashSet<>(expectedMentions);
        Set<User> actualMentionsSet = new HashSet<>(createdTweet.getUserMentions());
        Assertions.assertEquals(expectedMentionsSet.size(), actualMentionsSet.size());
        Assertions.assertEquals(expectedMentionsSet, actualMentionsSet);

        Set<String> expectedTags = new HashSet<>(Set.of(hashtagFromDb, additionalHashtag));
        Assertions.assertEquals(expectedTags.size(), createdTweet.getHashtags().size());
        for (Hashtag h: createdTweet.getHashtags()) {
            Assertions.assertTrue(expectedTags.contains(h.getLabel()));
        }
    }
}