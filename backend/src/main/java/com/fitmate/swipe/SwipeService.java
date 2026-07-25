package com.fitmate.swipe;

import com.fitmate.common.BadRequestException;
import com.fitmate.common.ConflictException;
import com.fitmate.match.Match;
import com.fitmate.match.MatchRepository;
import com.fitmate.swipe.dto.SwipeRequest;
import com.fitmate.swipe.dto.SwipeResult;
import com.fitmate.user.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public SwipeService(SwipeRepository swipeRepository,
                        MatchRepository matchRepository,
                        UserRepository userRepository) {
        this.swipeRepository = swipeRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    /**
     * Record a swipe. On a mutual LIKE a {@link Match} is created. The swiper's
     * cached discovery feed is evicted so the consumed card does not reappear.
     */
    @Transactional
    @CacheEvict(cacheNames = "discoveryFeed", key = "#swiperId")
    public SwipeResult swipe(Long swiperId, SwipeRequest request) {
        Long targetId = request.targetId();

        if (swiperId.equals(targetId)) {
            throw new BadRequestException("You cannot swipe on yourself");
        }
        if (!userRepository.existsById(targetId)) {
            throw new BadRequestException("Target user does not exist");
        }
        if (swipeRepository.findBySwiperIdAndTargetId(swiperId, targetId).isPresent()) {
            throw new ConflictException("You have already swiped on this user");
        }

        swipeRepository.save(Swipe.builder()
                .swiperId(swiperId)
                .targetId(targetId)
                .direction(request.direction())
                .build());

        if (request.direction() != SwipeDirection.LIKE) {
            return SwipeResult.noMatch();
        }

        boolean mutualLike = swipeRepository
                .existsBySwiperIdAndTargetIdAndDirection(targetId, swiperId, SwipeDirection.LIKE);
        if (!mutualLike) {
            return SwipeResult.noMatch();
        }

        Match match = createMatch(swiperId, targetId);
        return SwipeResult.matched(match.getId());
    }

    private Match createMatch(Long a, Long b) {
        long low = Math.min(a, b);
        long high = Math.max(a, b);
        return matchRepository.findByUserAIdAndUserBId(low, high)
                .orElseGet(() -> {
                    try {
                        return matchRepository.save(Match.builder()
                                .userAId(low)
                                .userBId(high)
                                .build());
                    } catch (DataIntegrityViolationException race) {
                        // Concurrent mutual swipe created the row first; reuse it.
                        return matchRepository.findByUserAIdAndUserBId(low, high)
                                .orElseThrow(() -> race);
                    }
                });
    }
}
