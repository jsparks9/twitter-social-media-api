package com.cooksys.social_network_api.controllers.user;

import com.cooksys.social_network_api.dtos.CredentialsDto;
import com.cooksys.social_network_api.dtos.UserResponseDto;
import com.cooksys.social_network_api.entities.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    private final String CONTENT_TYPE = "application/json";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @PersistenceContext
    private EntityManager entityManager;

    private <T> T getRandom(Class<T> clazz, String tableName, boolean useDeleteCondition) {
        String getQuery = "SELECT t FROM " + tableName + " t";
        if (useDeleteCondition) getQuery += " WHERE t.deleted = false";
        List<T> res = entityManager.createQuery(getQuery, clazz).getResultList();
        Random random = new Random();
        return res.get(random.nextInt(res.size()));
    }
    private <T> T getRandom(Class<T> clazz, String tableName) {
        return getRandom(clazz, tableName, true);
    }

    @Test
    void verify_count_and_non_null_of_getAllUsers() throws Exception {
        final String query = "SELECT COUNT(q) FROM User q where q.deleted = false";
        long expectedSize = entityManager.createQuery(query, Long.class)
                .getSingleResult();
        String resp = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize((int) expectedSize)))
                .andReturn().getResponse().getContentAsString();
        List<UserResponseDto> userRespList =
                objectMapper.readValue(resp, new TypeReference<>() {});
        Assertions.assertEquals(expectedSize, userRespList.size());
        for (UserResponseDto dto: userRespList) {
            Assertions.assertNotNull(dto.getUsername());
            Assertions.assertNotNull(dto.getProfile());
            Assertions.assertNotNull(dto.getProfile().getEmail());
        }
    }

    @Test
    void get_user_by_username() throws Exception {
        String queryText = "SELECT u FROM User u WHERE u.deleted = false";
        TypedQuery<User> query = entityManager.createQuery(queryText, User.class);
        query.setMaxResults(1);
        User expectedUser = query.getResultList().get(0);
        Assertions.assertNotNull(expectedUser, "This test needs a user to be in the database.");
        Assertions.assertNotNull(expectedUser.getCredentials());
        final String USERNAME = expectedUser.getCredentials().getUsername();

        String resp = mockMvc.perform(get("/users/@{username}", USERNAME))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserResponseDto userResp = objectMapper.readValue(resp, UserResponseDto.class);
        Assertions.assertNotNull(userResp);
        Assertions.assertNotNull(userResp.getProfile());
        Assertions.assertNotNull(userResp.getUsername());
        Assertions.assertEquals(expectedUser.getProfile().getEmail(), userResp.getProfile().getEmail());
        Assertions.assertEquals(expectedUser.getCredentials().getUsername(), userResp.getUsername());
        assertTimeEquals(expectedUser.getJoined(), userResp.getJoined());
    }

    public static void assertTimeEquals(Timestamp expectedTime, Timestamp actualTime) {
        long expectedMilli = expectedTime.toInstant().getEpochSecond() + (expectedTime.getNanos() / 1_000_000);
        long actualMilli = actualTime.toInstant().getEpochSecond() + (actualTime.getNanos() / 1_000_000);
        Assertions.assertEquals(expectedMilli, actualMilli, "" + expectedTime + " does not match " + actualTime);
    }

    @Test
    @Transactional  // Prevents Hibernate Table entities from disappearing after one .get() call
    void followUserByUserName() throws Exception {
        final String HTTP_URI = "/users/@{username}/follow";

        final String query = "SELECT COUNT(q) FROM User q where q.deleted = false";
        long userCountInDatabase = entityManager.createQuery(query, Long.class).getSingleResult();
        Assertions.assertTrue(userCountInDatabase > 1,
                "This test requires at least 2 users to be in the database.");
        User userFollowing = getRandom(User.class, "User");
        User userBeingFollowed;
        do {
            userBeingFollowed = getRandom(User.class, "User");
        } while (userFollowing.equals(userBeingFollowed) || userFollowing.getFollowing().contains(userBeingFollowed));
        final String userBeingFollowedUsername = userBeingFollowed.getCredentials().getUsername();
        Assertions.assertFalse(userFollowing.getFollowing().contains(userBeingFollowed));
        Assertions.assertFalse(userBeingFollowed.getFollowers().contains(userFollowing));

        CredentialsDto reqDto = new CredentialsDto();
        reqDto.setUsername(userFollowing.getCredentials().getUsername());
        reqDto.setPassword(userFollowing.getCredentials().getPassword());
        String request = objectMapper.writeValueAsString(reqDto);

        String resp = mockMvc.perform(post(HTTP_URI, userBeingFollowedUsername)
                        .contentType(CONTENT_TYPE)
                        .content(request))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertEquals("", resp);

        final String getUserById = "SELECT q FROM User q where q.id = ";
        final long userFollowingId = userFollowing.getId();
        User userFollowingAfter = entityManager.createQuery(getUserById + userFollowingId, User.class).getSingleResult();
        final long userBeingFollowedId = userBeingFollowed.getId();
        User userBeingFollowedAfter = entityManager.createQuery(getUserById + userBeingFollowedId, User.class).getSingleResult();
        Assertions.assertNotEquals(userFollowingAfter.getId(), userBeingFollowedAfter.getId());
        Assertions.assertTrue(userFollowingAfter.getFollowing().contains(userBeingFollowedAfter));
        Assertions.assertTrue(userBeingFollowedAfter.getFollowers().contains(userFollowingAfter));
        List<String> usersFollowing = userBeingFollowedAfter.getFollowers().stream()
                .map(u -> u.getCredentials().getUsername())
                .sorted().collect(Collectors.toList());
        List<String> usersBeingFollowed = userFollowingAfter.getFollowing().stream()
                .map(u -> u.getCredentials().getUsername())
                .sorted().collect(Collectors.toList());

        final String HTTP_GET_FOLLOWERS = "/users/@{username}/followers";
        final String HTTP_GET_FOLLOWING = "/users/@{username}/following";

        resp = mockMvc.perform(get(HTTP_GET_FOLLOWERS, userBeingFollowedUsername))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<String> followers = objectMapper.readValue(resp, new TypeReference<List<UserResponseDto>>() {})
                .stream().map(UserResponseDto::getUsername)
                .sorted().collect(Collectors.toList());
        Assertions.assertEquals(usersFollowing, followers);

        resp = mockMvc.perform(get(HTTP_GET_FOLLOWING, userFollowing.getCredentials().getUsername()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<String> following = objectMapper.readValue(resp, new TypeReference<List<UserResponseDto>>() {})
                .stream().map(UserResponseDto::getUsername)
                .sorted().collect(Collectors.toList());
        Assertions.assertEquals(usersBeingFollowed, following);
    }
}
