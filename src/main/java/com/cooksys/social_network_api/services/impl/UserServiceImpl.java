package com.cooksys.social_network_api.services.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cooksys.social_network_api.dtos.CredentialsDto;
import com.cooksys.social_network_api.dtos.ProfileDto;
import com.cooksys.social_network_api.dtos.TweetResponseDto;
import com.cooksys.social_network_api.dtos.UserRequestDto;
import com.cooksys.social_network_api.dtos.UserResponseDto;
import com.cooksys.social_network_api.entities.Credentials;
import com.cooksys.social_network_api.entities.Tweet;
import com.cooksys.social_network_api.entities.User;
import com.cooksys.social_network_api.exceptions.BadRequestException;
import com.cooksys.social_network_api.exceptions.NotFoundException;
import com.cooksys.social_network_api.mappers.CredentialsMapper;
import com.cooksys.social_network_api.mappers.ProfileMapper;
import com.cooksys.social_network_api.mappers.TweetMapper;
import com.cooksys.social_network_api.mappers.UserMapper;
import com.cooksys.social_network_api.repositories.TweetRepository;
import com.cooksys.social_network_api.repositories.UserRepository;
import com.cooksys.social_network_api.services.UserService;
import com.cooksys.social_network_api.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final TweetRepository tweetRepo;
    private final TweetMapper tweetMapper;
    private final ValidateService validateService;
    private final CredentialsMapper credsMapper;
    private final ProfileMapper profileMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.entitiesToDtos(userRepo.findByDeletedFalse());
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepo.findByCredentials_UsernameAndDeletedFalse(username).orElseThrow(() ->
                new NotFoundException(
                        "User with username '" + username + "' not found or has been deleted."
                ));
        return userMapper.entityToDto(user);
    }

    // TODO : Should this include replies to user?
    @Override
    public List<TweetResponseDto> getUserFeed(String username) {
        Credentials fakeCreds = new Credentials();
        fakeCreds.setUsername(username);
        User targetUser = validateService.findUser(fakeCreds);
        List<String> userList = targetUser.getFollowing().stream()
                .map(u -> u.getCredentials().getUsername())
                .collect(Collectors.toList());
        userList.add(username);
        List<Tweet> feedTweets = tweetRepo.findAllTweetsByUsernames(userList);
        feedTweets.sort(Comparator.comparing(Tweet::getPosted));
        Collections.reverse(feedTweets);
        return tweetMapper.entitiesToDtos(feedTweets);
    }

    @Override
    public void followUserByUserName(CredentialsDto credentialsDto, String username) {
        Credentials creds = credsMapper.credentialsDtoToEntity(credentialsDto);
        User requestingUser = validateService.findUser(creds);
        validateService.validateUser(requestingUser, creds);
        // User is now "logged in"
        Credentials fakeCreds = new Credentials();
        fakeCreds.setUsername(username);
        User userBeingFollowed = validateService.findUser(fakeCreds);  // Just get the other user and verify they exist
        if (requestingUser.getFollowing().contains(userBeingFollowed))
            throw new BadRequestException("Already following user '" + username + "'.");
        if (requestingUser.equals(userBeingFollowed))
            throw new BadRequestException("User cannot follow themself.");
        userBeingFollowed.getFollowers().add(requestingUser);
        if (!requestingUser.getFollowing().contains(userBeingFollowed))
            requestingUser.getFollowing().add(userBeingFollowed);
        userRepo.saveAllAndFlush(Arrays.asList(userBeingFollowed, requestingUser));
    }

    @Override
    public List<UserResponseDto> getWhoUserFollows(String username) {
        Credentials fakeCreds = new Credentials();
        fakeCreds.setUsername(username);
        User targetUser = validateService.findUser(fakeCreds);
        return userMapper.entitiesToDtos(targetUser.getFollowing());
    }

    @Override
    public List<UserResponseDto> getWhoFollowsUser(String username) {
        Credentials fakeCreds = new Credentials();
        fakeCreds.setUsername(username);
        User targetUser = validateService.findUser(fakeCreds);
        return userMapper.entitiesToDtos(targetUser.getFollowers());
    }

    @Override
    public List<TweetResponseDto> getMentions(String username) {
        User user = userRepo.findByCredentials_UsernameAndDeletedFalse(username).orElseThrow(() ->
                new NotFoundException(
                        "User with username '" + username + "' not found or has been deleted."
                ));
        return tweetMapper.entitiesToDtos(tweetRepo.findByUserMentionsAndDeletedFalseOrderByPostedDesc(user));
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        CredentialsDto credentialsDto = userRequestDto.getCredentials();
        ProfileDto profileDto = userRequestDto.getProfile();

        // Reactivate user if credentials are valid
        Optional<User> optionalUser = userRepo.findByCredentials_UsernameAndCredentials_PasswordAndDeletedTrue(
                userRequestDto.getCredentials().getUsername(),
                userRequestDto.getCredentials().getPassword()
        );
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDeleted(false);
            userRepo.save(user);
            List<Tweet> userTweets = user.getTweets();
            userTweets.forEach(t -> t.setDeleted(false));
            tweetRepo.saveAll(userTweets);
            /*
             TODO : tweets deleted by user before deactivation are brought back here, so need to refactor to track both
             TODO : when the user deletes a tweet and when the tweet is made invisible by user deactivation.
             */
            return userMapper.entityToDto(user);
        }

        if (!validateService.validateUsernameAvailable(credentialsDto.getUsername())) {
          throw new BadRequestException("Username already exists.");
        }

        if (!validateService.validateEmailAvailable(profileDto.getEmail())) {
          throw new BadRequestException("Email already exists.");
        }


        User userToSave = new User();
        userToSave.setCredentials(credsMapper.credentialsDtoToEntity(credentialsDto));
        userToSave.setProfile(profileMapper.profileDtoToEntity(profileDto));

        userRepo.saveAndFlush(userToSave);

        return userMapper.entityToDto(userToSave);
    }

	@Override
	public List<TweetResponseDto> getTweetsByUser(String username) {
		
		if(!validateService.validateUsernameExists(username)) {
			throw new NotFoundException("Username does not exist");
		}
		
		return tweetMapper.entitiesToDtos(
				tweetRepo.findAllByAuthor_Credentials_UsernameAndDeletedFalseOrderByPostedDesc(username)
		);
	}

	@Override
	public UserResponseDto renameUser(UserRequestDto userRequestDto, String newUsername) {
		
		CredentialsDto credentialsDto = userRequestDto.getCredentials();
		
		if (credentialsDto == null || credentialsDto.getUsername() == null || credentialsDto.getUsername().isEmpty() ||
				credentialsDto.getPassword() == null || credentialsDto.getPassword().isEmpty()) {
			throw new BadRequestException("Need Credentials");
		}
		
		Optional<User> userToPatch = userRepo.findByCredentials_UsernameAndDeletedFalse(credentialsDto.getUsername());
		
		if(!userToPatch.isEmpty()) {
			validateService.validateUser(userToPatch.get(), credsMapper.credentialsDtoToEntity(credentialsDto));
		} else {
			throw new NotFoundException("User to patch Not Found");
		}
		
		ProfileDto profileDto = userRequestDto.getProfile();
		if (profileDto == null) {
			throw new BadRequestException("Invalid Profile.");
		}
		
		
		userToPatch.get().getCredentials().setUsername(newUsername);
		
		return userMapper.entityToDto(userToPatch.get());
		
	}

    @Override
    public UserResponseDto deleteUser(CredentialsDto credentialsDto, String username) {
        Credentials credentials = credsMapper.credentialsDtoToEntity(credentialsDto);
        User userToDelete = userRepo.findByCredentials_UsernameAndDeletedFalse(
                credentials.getUsername()).orElseThrow(() ->
                new NotFoundException("User not found or has been deleted.")
        );
        validateService.validateUser(userToDelete, credentials);
        List<Tweet> tweets = userToDelete.getTweets();
        tweets.forEach(t -> t.setDeleted(true));
        tweetRepo.saveAllAndFlush(tweets);
        userToDelete.setDeleted(true);
        userRepo.save(userToDelete);
        return userMapper.entityToDto(userToDelete);
    }

	@Override
	public void unfollowUser(String usernameToUnfollow, CredentialsDto credentialsDto) {
		
		Credentials credentials = credsMapper.credentialsDtoToEntity(credentialsDto);
		User userToUnfollowFrom = userRepo.findByCredentials_UsernameAndDeletedFalse(
				credentials.getUsername()).orElseThrow(() ->
				new NotFoundException("Username to unfollow from not found")
		);
		validateService.validateUser(userToUnfollowFrom, credentials);
		
		User userToUnfollow = 
				userRepo.findByCredentials_UsernameAndDeletedFalse(usernameToUnfollow).orElseThrow(() -> 
				new NotFoundException("Username to unfollow does not exist."));
		
		List<User> following = userToUnfollowFrom.getFollowing();
		
		if(!following.contains(userToUnfollow)) {
			throw new BadRequestException("No Such Follow Relation Exists");
		}
		
		following.remove(userToUnfollow);
		userToUnfollow.getFollowers().remove(userToUnfollowFrom);
		
		userRepo.saveAndFlush(userToUnfollowFrom);
		userRepo.saveAndFlush(userToUnfollow);
	}
}
