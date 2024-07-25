package com.cooksys.social_network_api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// HTTP response code 400
@AllArgsConstructor
@Getter
@Setter
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 8717524765921018451L; 

    private String message;

}
