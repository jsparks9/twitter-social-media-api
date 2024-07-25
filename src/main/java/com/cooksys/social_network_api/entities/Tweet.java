package com.cooksys.social_network_api.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name = "`tweet`") // specifies database table name
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incrementing
    @Column(name = "id", // defines column name in the database
            insertable = false, // this column is not included in generated INSERT statements
            updatable = false,  // this column is not included in generated UPDATE statements
            nullable = false
    )
    private Long id;
    // testing commit from new branch
    @ManyToOne
    private User author;

    @CreationTimestamp
    private Timestamp posted;

    @Column(nullable = false)
    private Boolean deleted = false;

    private String content;

    @ManyToOne
    private Tweet inReplyTo;

    @OneToMany(mappedBy = "inReplyTo")
    private List<Tweet> replies;
    
    @ManyToOne
    @JoinColumn(name = "`repostOf`")
    private Tweet repostOf;
    
    @OneToMany(mappedBy = "repostOf")
    private List<Tweet> reposts;

    @ManyToMany
    @JoinTable(
            name="`tweet_hashtags`",
            joinColumns = @JoinColumn(name = "tweet_id"),  // Tweet id
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")  // Hashtag id
    )
    private List<Hashtag> hashtags = new ArrayList<>();

    @ManyToMany(mappedBy = "likedTweets")
    private List<User> likedByUsers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_mentions",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userMentions = new ArrayList<>();

}
