package com.cooksys.social_network_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.social_network_api.entities.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
	
	Optional<Hashtag> findByLabel(String label);

	List<Hashtag> findAllByTweets_Id(Long id);

}
