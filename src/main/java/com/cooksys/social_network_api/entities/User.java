package com.cooksys.social_network_api.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "`user_table`") // specifies database table name
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incrementing
    @Column(name = "id", // defines column name in the database
            insertable = false, // this column is not included in generated INSERT statements
            updatable = false,  // this column is not included in generated UPDATE statements
            nullable = false
    )
    private Long id;

    @CreationTimestamp
    private Timestamp joined;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Embedded
    private Credentials credentials;

    @Embedded
    private Profile profile;

    @ManyToMany
    @JoinTable(name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),  // User id
            inverseJoinColumns = @JoinColumn(name = "tweet_id"))
    private List<Tweet> likedTweets = new ArrayList<>();

    @ManyToMany(mappedBy="userMentions")
    private List<Tweet> mentions = new ArrayList<>();

    @ManyToMany(mappedBy = "followers")
    private List<User> following = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "followers_following")
    private List<User> followers = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Tweet> tweets = new ArrayList<>();
}
