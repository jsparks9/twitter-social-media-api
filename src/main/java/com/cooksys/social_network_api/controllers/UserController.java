package com.cooksys.social_network_api.controllers;

import com.cooksys.social_network_api.dtos.CredentialsDto;
import com.cooksys.social_network_api.dtos.TweetResponseDto;
import com.cooksys.social_network_api.dtos.UserRequestDto;
import com.cooksys.social_network_api.dtos.UserResponseDto;
import com.cooksys.social_network_api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUserByUsername(
            @PathVariable("username") String username
    ) {
        return userService.getUserByUsername(username);
    }

    @GetMapping(path = "/@{username}/feed")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getUserFeed(
            @PathVariable("username") String username
    ) {
        return userService.getUserFeed(username);
    }

    @PostMapping(path = "/@{username}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void followUserByUserName(
            @PathVariable("username") String username,
            @RequestBody @Valid CredentialsDto credentialsDto
    ) {
        userService.followUserByUserName(credentialsDto, username);
    }

    @GetMapping(path = "/@{username}/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getWhoFollowsUser(
            @PathVariable("username") String username
    ) {
        return userService.getWhoFollowsUser(username);
    }

    @GetMapping(path = "/@{username}/following")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getWhoUserFollows(
            @PathVariable("username") String username
    ) {
        return userService.getWhoUserFollows(username);
    }

    @GetMapping("/@{username}/mentions")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getMentions(
            @PathVariable("username") String username
    ) {
        return userService.getMentions(username);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(
            @RequestBody @Valid UserRequestDto userRequestDto
    ) {
    	return userService.createUser(userRequestDto);
    }
    
    @GetMapping("/@{username}/tweets")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getTweetsByUser(
            @PathVariable("username") String username
    ) {
    	return userService.getTweetsByUser(username);
    }
    
    @PatchMapping("/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto renameUser(
    		@RequestBody UserRequestDto userRequestDto,  // Validation moved to method
    		@PathVariable("username") String newUsername) {
    	return userService.renameUser(userRequestDto, newUsername);
    }

    @DeleteMapping("/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto deleteUser(
            @RequestBody @Valid CredentialsDto credentialsDto,
            @PathVariable("username") String username) {
        return userService.deleteUser(credentialsDto, username);
    }
    
    @PostMapping("/@{username}/unfollow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowUser(
    		@PathVariable("username") String usernameToUnfollow,
    		@RequestBody @Valid CredentialsDto credentialsDto
    ) {
    	userService.unfollowUser(usernameToUnfollow, credentialsDto);
    }
}
