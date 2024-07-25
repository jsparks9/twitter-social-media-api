package com.cooksys.social_network_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cooksys.social_network_api.entities.Tweet;
import com.cooksys.social_network_api.entities.User;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    List<Tweet> findAllByDeletedFalse();

    Optional<Tweet> findByIdAndDeletedFalse(Long id);

    List<Tweet> findByHashtags_LabelAndDeletedFalseOrderByPostedDesc(String label);

    List<Tweet> findByUserMentionsAndDeletedFalseOrderByPostedDesc(User mentionedUser);

    List<Tweet> findAllByAuthor_Credentials_UsernameAndDeletedFalseOrderByPostedDesc(String username);

    @Query("SELECT t FROM Tweet t WHERE t.author.credentials.username IN :usernames")
    List<Tweet> findAllTweetsByUsernames(@Param("usernames") List<String> usernames);
    
    List<Tweet> findAllTweetsByInReplyToAndDeletedFalse(Tweet tweet);

}
