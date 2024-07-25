package com.cooksys.social_network_api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class TweetRequestDto {

    @NotNull
    @NotBlank(message = "Tweet cannot be blank.")
    private String content;

    @NotNull
    @Valid
    private CredentialsDto credentials;
}
