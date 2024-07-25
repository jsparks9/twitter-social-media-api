package com.cooksys.social_network_api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDto {

	@Valid
	@NotNull(message="Credentials cannot be null.")
	private CredentialsDto credentials;
	
	@Valid
	@NotNull(message="Profile cannot be null.")
	private ProfileDto profile;
	
}
