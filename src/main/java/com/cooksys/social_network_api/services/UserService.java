package com.cooksys.social_network_api.services;

import com.cooksys.social_network_api.dtos.CredentialsDto;
import com.cooksys.social_network_api.dtos.TweetResponseDto;
import com.cooksys.social_network_api.dtos.UserRequestDto;
import com.cooksys.social_network_api.dtos.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    List<TweetResponseDto> getUserFeed(String username);

    UserResponseDto getUserByUsername(String username);

    void followUserByUserName(CredentialsDto credentialsDto, String username);

    List<UserResponseDto> getWhoUserFollows(String username);

    List<UserResponseDto> getWhoFollowsUser(String username);

    List<TweetResponseDto> getMentions(String username);

    UserResponseDto createUser(UserRequestDto userRequestDto);

	List<TweetResponseDto> getTweetsByUser(String username);

	UserResponseDto renameUser(UserRequestDto userRequestDto, String newUsername);

    UserResponseDto deleteUser(CredentialsDto credsDto, String username);

	void unfollowUser(String usernameToUnfollow, CredentialsDto credentialsDto);

}
