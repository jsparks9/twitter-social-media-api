package com.cooksys.social_network_api.services.impl;

import com.cooksys.social_network_api.dtos.HashtagResponseDto;
import com.cooksys.social_network_api.entities.Hashtag;
import com.cooksys.social_network_api.mappers.HashtagMapper;
import com.cooksys.social_network_api.repositories.HashtagRepository;
import com.cooksys.social_network_api.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {
    private final HashtagRepository hashtagRepo;
    private final HashtagMapper hashtagMapper;

    private Hashtag getOrCreateHashtagWithLabel(String label) {
        Optional<Hashtag> optionalHashtag = hashtagRepo.findByLabel(label);
        Hashtag hashtag;
        if (optionalHashtag.isEmpty()) {
            hashtag = new Hashtag();
            hashtag.setLabel(label);
        } else {
            hashtag = optionalHashtag.get();
        }
        return hashtag;
    }

    @Override
    public Hashtag createHashtag(String label) {
        Hashtag hashtag = getOrCreateHashtagWithLabel(label);
        return hashtagRepo.save(hashtag);
    }

    @Override
    public List<Hashtag> createHashtags(List<String> labels) {
        ArrayList<Hashtag> hashtags = new ArrayList<>(labels.size());
        for (String label : labels) {
            Hashtag hashtag = getOrCreateHashtagWithLabel(label);
            hashtags.add(hashtag);
        }
        return hashtagRepo.saveAll(hashtags);
    }

    @Override
    public List<HashtagResponseDto> findHashtagsByTweetId(Long id) {
        return hashtagMapper.entitiesToDtos(hashtagRepo.findAllByTweets_Id(id));
    }

    @Override
    public List<HashtagResponseDto> getAllHashtags() {
        return hashtagMapper.entitiesToDtos(hashtagRepo.findAll());
    }

}
