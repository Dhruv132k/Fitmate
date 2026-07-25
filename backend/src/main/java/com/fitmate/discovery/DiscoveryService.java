package com.fitmate.discovery;

import com.fitmate.discovery.dto.CandidateCard;
import com.fitmate.discovery.dto.FeedResponse;
import com.fitmate.user.User;
import com.fitmate.user.UserRepository;
import com.fitmate.user.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscoveryService {

    private static final int MAX_FEED_SIZE = 30;

    private final UserRepository userRepository;
    private final UserService userService;

    public DiscoveryService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Build the swipe deck for a user. Cached in Redis (cache name
     * {@code discoveryFeed}, key = user id) and evicted whenever the user
     * swipes or edits their profile, so the deck stays fresh without hitting
     * the database on every card load.
     */
    @Cacheable(cacheNames = "discoveryFeed", key = "#userId")
    @Transactional(readOnly = true)
    public FeedResponse getFeed(Long userId) {
        User me = userService.getById(userId);
        List<User> candidates = userRepository.findCandidates(
                userId, me.getWorkoutGoal(), me.getGymName(), me.getCity());

        List<CandidateCard> cards = new ArrayList<>();
        for (User c : candidates) {
            if (cards.size() >= MAX_FEED_SIZE) {
                break;
            }
            cards.add(toCard(me, c));
        }
        return new FeedResponse(cards);
    }

    private CandidateCard toCard(User me, User other) {
        List<String> reasons = new ArrayList<>();
        int score = 0;

        if (me.getWorkoutGoal() != null && me.getWorkoutGoal() == other.getWorkoutGoal()) {
            reasons.add("Same goal: " + humanize(other.getWorkoutGoal().name()));
            score += 50;
        }
        if (StringUtils.hasText(me.getGymName())
                && me.getGymName().equalsIgnoreCase(other.getGymName())) {
            reasons.add("Trains at " + other.getGymName());
            score += 35;
        }
        if (StringUtils.hasText(me.getCity())
                && me.getCity().equalsIgnoreCase(other.getCity())) {
            reasons.add("In " + other.getCity());
            score += 15;
        }
        if (me.getExperienceLevel() != null && me.getExperienceLevel() == other.getExperienceLevel()) {
            score += 10;
        }

        return new CandidateCard(
                other.getId(), other.getName(), other.getAge(), other.getBio(),
                other.getWorkoutGoal(), other.getExperienceLevel(), other.getGymName(),
                other.getCity(), reasons, Math.min(score, 100));
    }

    private String humanize(String enumName) {
        return enumName.toLowerCase().replace('_', ' ');
    }
}
