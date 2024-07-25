package com.cooksys.social_network_api.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@Data
public class Credentials {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
}
