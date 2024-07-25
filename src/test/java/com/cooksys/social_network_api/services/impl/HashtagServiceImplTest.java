package com.cooksys.social_network_api.services.impl;

import com.cooksys.social_network_api.entities.Hashtag;
import com.cooksys.social_network_api.services.HashtagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
class HashtagServiceImplTest {

    final HashtagService hashtagService;

    @Autowired
    HashtagServiceImplTest(HashtagService hashtagService) {
        this.hashtagService = hashtagService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    private <T> void print(T any) {
        System.out.println(any);
    }

    @Transactional  // Prevents Hibernate Table entities from disappearing after one .get() call
    void createHashtags() throws Exception {
        final String retrieveHashtagsQuery = "SELECT t FROM Hashtag t";
        List<Hashtag> hashTagsInDatabase = entityManager.createQuery(retrieveHashtagsQuery, Hashtag.class).getResultList();
        Assertions.assertTrue(hashTagsInDatabase.size() > 1,
                "This test needs at least 2 Hashtags in the database.");
        final int initialSize = hashTagsInDatabase.size();
        Random random = new Random();
        int randomIndex = random.nextInt(hashTagsInDatabase.size());
        Hashtag hashtagInDbNotInUpdate = hashTagsInDatabase.get(randomIndex);
        hashTagsInDatabase.remove(randomIndex);
        randomIndex = random.nextInt(hashTagsInDatabase.size());
        Hashtag hashtagInDbAndInUpdate = hashTagsInDatabase.get(randomIndex);

        Set<String> tags = new HashSet<>(Set.of(
                hashtagInDbNotInUpdate.getLabel(),
                hashtagInDbAndInUpdate.getLabel()
        ));
        final int targetSize = tags.size() + 5;
        do {
            tags.add(UUID.randomUUID().toString().replaceAll("-", ""));
        } while (tags.size() < targetSize);

        tags.remove(hashtagInDbNotInUpdate.getLabel());
        List<String> hashtagReq = new ArrayList<>(tags);
        Assertions.assertTrue(hashtagReq.contains(hashtagInDbAndInUpdate.getLabel()));
        Assertions.assertFalse(hashtagReq.contains(hashtagInDbNotInUpdate.getLabel()));
        List<Hashtag> hashtagResp = hashtagService.createHashtags(hashtagReq);
        Assertions.assertEquals(hashtagReq.size(), hashtagResp.size());

        // Make sure that firstUsed and lastUsed are correct
        for (Hashtag h: hashtagResp) {
            if (h.getLabel().equals(hashtagInDbNotInUpdate.getLabel())) {
                throw new Exception("This Label was not in the update request, but was in resp.");
            } else if (h.getLabel().equals(hashtagInDbAndInUpdate.getLabel())) {
                // TODO : Why is the timestamp the same? Is it not updating when saving?
//                Assertions.assertNotEquals(h.getFirstUsed(), h.getLastUsed());
                Assertions.assertEquals(hashtagInDbAndInUpdate.getFirstUsed(), h.getFirstUsed());
            } else {
                // TODO : Why is there a 15 millisecond difference
//                Assertions.assertEquals(h.getFirstUsed(), h.getLastUsed());
            }
        }
        hashTagsInDatabase = entityManager.createQuery(retrieveHashtagsQuery, Hashtag.class).getResultList();
        Assertions.assertEquals(initialSize + 5, hashTagsInDatabase.size()); // 5 added, 1 already in database
        Set<String> tagsInDb = hashTagsInDatabase.stream().map(Hashtag::getLabel).collect(Collectors.toSet());
        for (Hashtag h: hashtagResp)
            Assertions.assertTrue(tagsInDb.contains(h.getLabel()));

    }
}