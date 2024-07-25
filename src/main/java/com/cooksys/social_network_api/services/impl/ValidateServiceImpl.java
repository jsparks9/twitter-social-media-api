package com.cooksys.social_network_api.services.impl;

import com.cooksys.social_network_api.entities.Credentials;
import com.cooksys.social_network_api.entities.User;
import com.cooksys.social_network_api.exceptions.NotAuthorizedException;
import com.cooksys.social_network_api.exceptions.NotFoundException;
import com.cooksys.social_network_api.repositories.HashtagRepository;
import com.cooksys.social_network_api.repositories.UserRepository;
import com.cooksys.social_network_api.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {
	
	private final UserRepository userRepository;
	
	private final HashtagRepository hashtagRepository;
	
	@Override
	public boolean validateTagExists(String label) {
		return !hashtagRepository.findByLabel(label).isEmpty();
	}
	
	@Override
	public boolean validateUsernameExists(String username) {
		return !userRepository.findByCredentials_UsernameAndDeletedFalse(username).isEmpty();
		
	}
	
	@Override
	public boolean validateUsernameAvailable(String username) {
		return !validateUsernameExists(username);
	}

	@Override
	public User findUser(Credentials credentials) {
		final String username = credentials.getUsername();
		return userRepository.
				findByCredentials_UsernameAndDeletedFalse(username).orElseThrow(() -> new NotFoundException(
						"User with username '" + username + "' not found or has been deleted."
				));
	}

	// Only compares passwords, not username
	@Override
	public void validateUser(User user, Credentials creds) {
		final String actualPassword = user.getCredentials().getPassword();
		final String enteredPassword = creds.getPassword();
		if (!actualPassword.equals(enteredPassword))
			throw new NotAuthorizedException(
					"Authentication failed. The password you entered is incorrect. " +
							"Please verify your password and try again.");
	}

	@Override
	public boolean validateEmailAvailable(String email) {
		return userRepository.findByProfile_EmailAndDeletedFalse(email).isEmpty();
	}
}
