package com.cooksys.social_network_api.services;

import com.cooksys.social_network_api.entities.Credentials;
import com.cooksys.social_network_api.entities.User;

public interface ValidateService {
	
	boolean validateTagExists(String label);
	
	boolean validateUsernameExists(String username);
	
	boolean validateUsernameAvailable(String username);

	User findUser(Credentials credentials);

	void validateUser(User user, Credentials creds);
	
	boolean validateEmailAvailable(String email);

}
