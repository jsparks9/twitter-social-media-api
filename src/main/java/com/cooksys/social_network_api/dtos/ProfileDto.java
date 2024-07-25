package com.cooksys.social_network_api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProfileDto {
	
	private String firstName;
	
	private String lastName;
	
	@NotNull(message="Email is required.")
	@NotBlank(message="Email is required.")
	private String email;
	
	private String phone;

}
