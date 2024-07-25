package com.cooksys.social_network_api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CredentialsDto {

	@NotNull
	@NotBlank(message = "Username cannot be blank.")
	private String username;

	@NotNull
	@NotBlank(message = "Password cannot be blank.")
	private String password;

}
