package com.cooksys.social_network_api.services.impl;

import com.cooksys.social_network_api.dtos.*;
import com.cooksys.social_network_api.entities.Credentials;
import com.cooksys.social_network_api.entities.Hashtag;
import com.cooksys.social_network_api.entities.Tweet;
import com.cooksys.social_network_api.entities.User;
import com.cooksys.social_network_api.exceptions.NotAuthorizedException;
import com.cooksys.social_network_api.exceptions.NotFoundException;
import com.cooksys.social_network_api.mappers.CredentialsMapper;
import com.cooksys.social_network_api.mappers.TweetMapper;
import com.cooksys.social_network_api.mappers.UserMapper;
import com.cooksys.social_network_api.repositories.TweetRepository;
import com.cooksys.social_network_api.repositories.UserRepository;
import com.cooksys.social_network_api.services.HashtagService;
import com.cooksys.social_network_api.services.TweetService;
import com.cooksys.social_network_api.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final ValidateService validateService;
    private final CredentialsMapper credentialsMapper;
    private final UserRepository userRepo;
    private final HashtagService hashtagService;
    private final UserMapper userMapper;

    @Override
    public List<TweetResponseDto> getAllTweets() {
        return tweetMapper.entitiesToDtos(tweetRepository.findAllByDeletedFalse());
    }

    @Override
    public TweetResponseDto getTweet(Long id) {
        Optional<Tweet> foundTweet = tweetRepository.findById(id);
        if(foundTweet.isEmpty()){
            throw new NotFoundException("No tweet found with id + " + id);
        }
        return tweetMapper.entityToDto(foundTweet.get());
    }

    @Override
    public List<UserResponseDto> getTweetLikes(Long id) {
        Tweet foundTweet = tweetRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                "Tweet does not exist or was deleted."
        ));

        Set<User> users = new HashSet<>();
        for(User u: foundTweet.getLikedByUsers()){
            if(!u.getDeleted()){
                users.add(u);
            }
        }
        return userMapper.entitiesToDtos(users.stream().toList());
    }

    @Override
    public ContextResponseDto getTweetContext(Long id) {

        Tweet foundTweet = tweetRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "No tweet found with id + " + id
        ));
        if(foundTweet.getDeleted()){  // if deleted == true
            throw new NotFoundException("Tweet has been deleted");
        }

        List<Tweet> replies = foundTweet.getReplies();
        for (int i = 0; i < replies.size(); i++)
            replies.addAll(replies.get(i).getReplies());

        replies.removeIf(Tweet::getDeleted);
        replies.sort(Comparator.comparing(Tweet::getPosted));  // SORTING BY TIMESTAMP

        ContextResponseDto context = new ContextResponseDto();
        context.setTarget(tweetMapper.entityToDto(foundTweet));
        context.setAfter(tweetMapper.entitiesToDtos(replies));

        List<Tweet> beforeTweets = new ArrayList<>();
        Tweet currentTweet = foundTweet;
        while(currentTweet.getInReplyTo() != null){
            if(!currentTweet.getInReplyTo().getDeleted()){
                beforeTweets.add(currentTweet.getInReplyTo());
            }
            currentTweet = currentTweet.getInReplyTo();
        }
        // SORTING BY TIMESTAMP
        beforeTweets.sort((t1, t2) -> t1.getPosted().compareTo(t2.getPosted()));

        context.setBefore(tweetMapper.entitiesToDtos(beforeTweets));


        return context;
    }

    @Override
    public void findMentionsAndTags(String content, List<String> mentions, List<String> tags) {
        Pattern mentionPattern = Pattern.compile("@(\\w+)");
        Pattern tagPattern = Pattern.compile("#(\\w+)");

        Matcher mentionMatcher = mentionPattern.matcher(content);
        Matcher tagMatcher = tagPattern.matcher(content);
        while (mentionMatcher.find()) mentions.add(mentionMatcher.group(1));
        while (tagMatcher.find()) tags.add(tagMatcher.group(1));
    }

    private TweetResponseDto postTweet(TweetRequestDto tweetPostDto, Tweet inReplyTo) {
        Credentials creds = credentialsMapper.credentialsDtoToEntity(tweetPostDto.getCredentials());
        User user = validateService.findUser(creds);  // Find database entry by username
        validateService.validateUser(user, creds); // Verify database password matches

        String tweetContent = tweetPostDto.getContent();
        List<String> hashtagsFromTweetContent = new ArrayList<>();
        List<String> mentionsFromTweetContent = new ArrayList<>();
        findMentionsAndTags(tweetContent, mentionsFromTweetContent, hashtagsFromTweetContent);
        List<Hashtag> hashtags = hashtagService.createHashtags(hashtagsFromTweetContent);
        List<User> mentions = userRepo.findAllByCredentials_UsernameInAndDeletedFalse(mentionsFromTweetContent);
        Tweet newTweet = new Tweet();
        newTweet.setContent(tweetContent);
        if (inReplyTo != null) newTweet.setInReplyTo(inReplyTo);
        newTweet.setAuthor(user);
        newTweet.setHashtags(hashtags);
        newTweet.setUserMentions(mentions);
        newTweet = tweetRepository.save(newTweet);
        return tweetMapper.entityToDto(newTweet);
    }

    @Override
    public List<TweetResponseDto> findTweetsByHashtagLabel(String label) {
        List<TweetResponseDto> resp = tweetMapper.entitiesToDtos(tweetRepository
                .findByHashtags_LabelAndDeletedFalseOrderByPostedDesc(label));
        if (resp.isEmpty()) throw new NotFoundException("Tag with label '" + label + "' not found,");
        return resp;
    }

    @Override
    public TweetResponseDto postTweet(TweetRequestDto tweetRequestDto) {
        return postTweet(tweetRequestDto, null);
    }

    @Override
    public TweetResponseDto deleteTweet(CredentialsDto credentialsDto, Long id) {
        Credentials creds = credentialsMapper.credentialsDtoToEntity(credentialsDto);
        User user = validateService.findUser(creds);
        validateService.validateUser(user, creds);

        Tweet foundTweet = tweetRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                "Tweet with id " + id + " does not exist or has been deleted."
        ));

        if(!foundTweet.getAuthor().equals(user)){ // validates if tweet was made by provided user
            throw new NotAuthorizedException("User is not author of tweet");
        }

        foundTweet.setDeleted(true);
        tweetRepository.save(foundTweet);

        return tweetMapper.entityToDto(foundTweet);
    }

    @Override
    public TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetPostDto) {
        Tweet inReplyToTweet = tweetRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                "Tweet with id '" + id + "' doesn't exist or was deleted."
        ));
        return postTweet(tweetPostDto, inReplyToTweet);
    }

    @Override
    public void createLike(CredentialsDto credentialsDto, Long id) {
        Tweet foundTweet = tweetRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                "Tweet does not exist or was deleted."
        ));

        Credentials creds = credentialsMapper.credentialsDtoToEntity(credentialsDto);
        User user = validateService.findUser(creds);
        validateService.validateUser(user, creds);

        user.getLikedTweets().add(foundTweet);
        userRepo.save(user);

    }
  
    @Override
    public List<TweetResponseDto> getReposts(Long id) {
        Tweet tweet = tweetRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                "Tweet with id '" + id + "' doesn't exist or was deleted."
        ));
        return tweetMapper.entitiesToDtos(tweet.getReposts().stream()
                .filter(t -> !t.getDeleted())
                .collect(Collectors.toList()));
    }

    @Override
    public List<UserResponseDto> getTweetMentions(Long id) {
        Tweet tweet = tweetRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Tweet not found or has been deleted.")
        );
        return userMapper.entitiesToDtos(tweet.getUserMentions().stream()
                .filter(u -> !u.getDeleted())
                .collect(Collectors.toList()));
    }

    @Override
    public TweetResponseDto repostTweet(CredentialsDto credentialsDto, Long id) {
        Credentials creds = credentialsMapper.credentialsDtoToEntity(credentialsDto);
        User requestingUser = validateService.findUser(creds);
        validateService.validateUser(requestingUser, creds);
        Tweet tweetToRepost = tweetRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                "Tweet with id '" + id + "' doesn't exist or was deleted."
        ));

        Tweet repostingTweet = new Tweet();
        repostingTweet.setRepostOf(tweetToRepost);
        repostingTweet.setAuthor(requestingUser);
        repostingTweet.setContent(null);
        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(repostingTweet));
    }

    @Override
    public List<TweetResponseDto> getRepliesByID(Long id) {

      Tweet parentTweet = tweetRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> 
        new NotFoundException("Tweet with id '" + id + "' doesn't exist or was deleted."
      ));

      return tweetMapper.entitiesToDtos(tweetRepository.findAllTweetsByInReplyToAndDeletedFalse(parentTweet));
    }
}
