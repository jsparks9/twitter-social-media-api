package com.cooksys.social_network_api.config;

import com.cooksys.social_network_api.entities.*;
import com.cooksys.social_network_api.repositories.HashtagRepository;
import com.cooksys.social_network_api.repositories.TweetRepository;
import com.cooksys.social_network_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("local || test")
public class Seeder implements CommandLineRunner {
    private final HashtagRepository hashtagRepo;
    private final TweetRepository tweetRepo;
    private final UserRepository userRepo;

    private static final Data data = new Data();
    private static final Random random = new Random();


    public List<String> getUniqueEmails() throws Exception {
        Pattern pattern = Pattern.compile("(\\d*)(@)");
        List<String> emails = new ArrayList<>(List.of(data.getEmails()));
        List<String> uniqueEmails = new ArrayList<>();

//        for (int i=1; i< emails.size(); i++) emails.set(i, emails.get(0));  // Make duplicates
        for (String e: emails) {  // Adds numbers to duplicate emails
            while (uniqueEmails.contains(e)) {
                Matcher matcher = pattern.matcher(e);
                e = matcher.replaceAll(random.nextInt(300) + "$2");
            }
            uniqueEmails.add(e);
        }
        if (uniqueEmails.size() != (new HashSet<>(uniqueEmails)).size())
            throw new Exception("Emails not unique");
        return uniqueEmails;
    }

    public List<String> getUniqueUsernames() throws Exception {
        List<String> usernames = Arrays.stream(data.getNames())
                .map(s -> s.split("\\s+")[0]).toList();
        List<String> uniqueUsernames = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d*)$");

        for (String s: usernames) {
            int max = 10;
            while (uniqueUsernames.contains(s)) {
                Matcher matcher = pattern.matcher(s);
                s = matcher.replaceAll(""+random.nextInt(max));
                max = Math.min(10_000, max * 10);
            }
            uniqueUsernames.add(s);
        }
        if (uniqueUsernames.size() != (new HashSet<>(uniqueUsernames)).size())
            throw new Exception("Usernames not unique");
        return uniqueUsernames;
    }

    public User userGenerator(String username, String email, String name) {
        final double probabilityOfUsingName = 0.7;
        final double conditionalProbUsingOnlyFirstName = 0.2;
        final double probabilityOfPhoneNo = 0.8;
        Profile profile = new Profile();
        if (random.nextDouble() < probabilityOfUsingName) {
            profile.setFirstName(name.split("\\s+")[0]);
            if (random.nextDouble() >= conditionalProbUsingOnlyFirstName)
                profile.setLastName(name.split("\\s+")[1]);
        }
        if (random.nextDouble() < probabilityOfPhoneNo) {
            int areaCode = random.nextInt(800 - 200) + 200;
            int exchange = random.nextInt(900 - 100) + 100;
            int subscriberNumber = random.nextInt(9000) + 1000;
            String phoneNo = String.format("(%03d) %03d-%04d", areaCode, exchange, subscriberNumber);
            profile.setPhone(phoneNo);
        }
        profile.setEmail(email);
        User user = new User();
        user.setProfile(profile);
        Credentials creds = new Credentials();
        creds.setPassword(UUID.randomUUID().toString().replaceAll("-",""));
        creds.setUsername(username);
        user.setCredentials(creds);
        return userRepo.save(user);
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> emails = getUniqueEmails();
        List<String> names = Arrays.asList(data.getNames());
        List<String> usernames = getUniqueUsernames();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < emails.size(); i++)
            users.add(userGenerator(usernames.get(i), emails.get(i), names.get(i)));

        Set<Integer> deleteInds = new HashSet<>();
        while (deleteInds.size() < 5)
            deleteInds.add(random.nextInt(users.size()));
        for (int i: deleteInds)
            users.get(i).setDeleted(true);

        userRepo.saveAllAndFlush(users);
        Map<String, List<String>> tagMapper = new HashMap<>() {{
            put("radioStationTag",   new ArrayList<>(Arrays.asList(data.getRadioStationTag())));
            put("exoticLocationTag", new ArrayList<>(Arrays.asList(data.getExoticLocationTag())));
            put("actionTag",         new ArrayList<>(Arrays.asList(data.getActionTag())));
            put("foodTag",           new ArrayList<>(Arrays.asList(data.getFoodTag())));
            put("animalTag",         new ArrayList<>(Arrays.asList(data.getAnimalTag())));
            put("countryTag",        new ArrayList<>(Arrays.asList(data.getCountryTag())));
            put("looksTag",          new ArrayList<>(Arrays.asList(data.getLooksTag())));
            put("activityTag",       new ArrayList<>(Arrays.asList(data.getActivityTag())));
            put("aboutTag",          new ArrayList<>(Arrays.asList(data.getAboutTag())));
        }};

        // TODO : Generate 10000 random tweets
        final double probabilityOfAdditionalMention = 0.2;
        final double probabilityOfAdditionalTag = 0.4;
        final double probabilityOfReply = 0.1;
        



        // Now, generate random tweets with filled-in hashtags and random mentions
        // ----- HASHTAGS -----

//        Hashtag hashtag1 = new Hashtag();
//        hashtag1.setLabel("eldenlord");
//
//        Hashtag hashtag2 = new Hashtag();
//        hashtag2.setLabel("mario");
//
//        Hashtag hashtag3 = new Hashtag();
//        hashtag3.setLabel("luigi");
//
//        Hashtag hashtag4 = new Hashtag();
//        hashtag4.setLabel("whereiscortana");
//
//        hashtagRepo.saveAllAndFlush(Arrays.asList(hashtag1, hashtag2, hashtag3, hashtag4));
//
////	    // ----- TWEETS -----
//        // --- Start Tweet 1 ---
//        Tweet tweet1 = new Tweet();
//        tweet1.setAuthor(user1);
//        tweet1.setDeleted(false);
//        tweet1.setContent("This is some content 1 tweet1 #eldenlord #mario");
//        tweet1.setHashtags(Arrays.asList(hashtag1, hashtag2));
//        tweet1.setUserMentions(Arrays.asList(user1, user2));
//        tweetRepo.saveAndFlush(tweet1);
//
//        // --- Start Tweet 2 ---
//        Tweet tweet2 = new Tweet();
//        tweet2.setAuthor(user1);
//        tweet2.setDeleted(false);
//        tweet2.setContent("This is some content 2 tweet2 #eldenlord #mario");
//        tweet2.setHashtags(Arrays.asList(hashtag1, hashtag2));
//        tweet2.setInReplyTo(tweet1);
//        tweetRepo.saveAndFlush(tweet2);
//
//        // --- Start Tweet 3 ---
//        Tweet tweet3 = new Tweet();
//        tweet3.setAuthor(user2);
//        tweet3.setDeleted(false);
//        // Set Content @PARAM String
//        tweet3.setContent("This is some content 3 tweet3 #luigi #whereiscortana");
//        tweet3.setHashtags(Arrays.asList(hashtag3, hashtag4));
//        tweet3.setInReplyTo(tweet2);
//        tweetRepo.saveAndFlush(tweet3);
//
//        // --- Start Tweet 4 ---
//        Tweet tweet4 = new Tweet();
//        tweet4.setAuthor(user2);
//        tweet4.setDeleted(false);
//        // Set Content @PARAM String
//        tweet4.setContent("This is some content 4 tweet4");
//        tweet4.setInReplyTo(tweet3);
//        tweetRepo.saveAndFlush(tweet4);
//
//        // --- Start Tweet 5 ---
//        Tweet tweet5 = new Tweet();
//        tweet5.setAuthor(user3);
//        tweet5.setDeleted(false);
//        // Set Content @PARAM String
//        tweet5.setContent("This is some content 5 tweet5");
//        tweet5.setUserMentions(Arrays.asList(user1, user2));
//        tweet5.setInReplyTo(tweet4);
//        tweetRepo.saveAndFlush(tweet5);
//
//        // --- Start Tweet 6 ---
//        Tweet tweet6 = new Tweet();
//        tweet6.setAuthor(user3);
//        tweet6.setDeleted(false);
//        // Set Content @PARAM String
//        tweet6.setRepostOf(tweet5);
//        tweet6.setUserMentions(Arrays.asList(user1, user2));
//        tweet6.setInReplyTo(tweet2);
//        tweetRepo.saveAndFlush(tweet6);
//
//        // --- Start Tweet 7 ---
//        Tweet deletedTweet = new Tweet();
//        deletedTweet.setAuthor(user3);
//        deletedTweet.setDeleted(true);
//        // Set Content @PARAM String
//        deletedTweet.setContent("This is a deleted tweet (User3) tweet7");
//        deletedTweet.setUserMentions(Arrays.asList(user1, user2));
//        tweetRepo.saveAndFlush(deletedTweet);
//
//        // ----- LIST of Tweets + Adding to User# -----
//        List<Tweet> user1Tweets = List.of(tweet1, tweet2);
//        user1.setTweets(user1Tweets);
//        userRepo.saveAndFlush(user1);
//
//        List<Tweet> user2Tweets = List.of(tweet3, tweet4);
//        user2.setTweets(user2Tweets);
//        userRepo.saveAndFlush(user2);
//
//        List<Tweet> user3Tweets = List.of(tweet5, tweet6);
//        user3.setTweets(user3Tweets);
//        userRepo.saveAndFlush(user3);
//
//        // ----- List of Liked Tweets -----
//        user1.setLikedTweets(user3Tweets);
//        userRepo.saveAndFlush(user1);
//
//        user2.setLikedTweets(user1Tweets);
//        user2.setLikedTweets(user2Tweets);
//        userRepo.saveAndFlush(user2);
//
//        user3.setLikedTweets(user2Tweets);
//        userRepo.saveAndFlush(user3);
//
//        deletedUser.setLikedTweets(user2Tweets);
//        userRepo.saveAndFlush(deletedUser);
//
//        // ----- List of Following -----
//        List<User> followingList = List.of(user2, user3, user4);
//        user1.setFollowing(followingList);
//        userRepo.saveAndFlush(user1);
//        // ----- List of Followers -----
//        List<User> followersList = List.of(user3, user5);
//        user1.setFollowers(followersList);
//        userRepo.saveAndFlush(user1);
//
//        // ----- Tweet Mentions -----
//        Tweet mention1 = new Tweet();
//        mention1.setAuthor(user2);
//        mention1.setDeleted(false);
//        // Set Content @PARAM String
//        mention1.setContent("This is some content for tweet mention 1");
//        tweetRepo.saveAndFlush(mention1);
//
//        // Following
//        List<User> following_1 = List.of(user2, user3, user4, deletedUser);
//        user1.setFollowing(following_1);
//
//        List<User> followers_1 = List.of(user5, deletedUser);
//        user1.setFollowers(followers_1);
//        userRepo.saveAndFlush(user1);
    }
}
