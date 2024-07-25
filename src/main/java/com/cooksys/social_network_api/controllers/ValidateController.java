package com.cooksys.social_network_api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.cooksys.social_network_api.services.ValidateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {
	
	private final ValidateService validateService;
	
	@GetMapping("/tag/exists/{label}")
	@ResponseStatus(HttpStatus.OK)
	public boolean validateTagExists(
			@PathVariable("label") String label
	) {
		return validateService.validateTagExists(label);
	}
	
	@GetMapping("/username/exists/@{username}")
	@ResponseStatus(HttpStatus.OK)
	public boolean validateUsernameExists(
			@PathVariable("username") String username
	) {
		return validateService.validateUsernameExists(username);
	}
	
	@GetMapping("/username/available/@{username}")
	@ResponseStatus(HttpStatus.OK)
	public boolean validateUsernameAvailable(
			@PathVariable("username") String username
	) {
		return validateService.validateUsernameAvailable(username);
	}
}
