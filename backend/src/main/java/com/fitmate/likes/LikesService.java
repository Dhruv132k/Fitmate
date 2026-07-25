package com.fitmate.likes;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmate.likes.dto.IncomingLike;
import com.fitmate.swipe.Swipe;
import com.fitmate.swipe.SwipeDirection;
import com.fitmate.swipe.SwipeRepository;
import com.fitmate.user.User;
import com.fitmate.user.UserService;

@Service
public class LikesService {
    
    private final SwipeRepository swipeRepository;
    private final UserService userService;

    public LikesService(SwipeRepository swipeRepository, UserService userService) {
        this.swipeRepository = swipeRepository;
        this.userService = userService;
    }

    /*People who have liked me and whom I have not swiped yet */
    @Transactional(readOnly = true)
    public List<IncomingLike> getIncomingLikes(Long userId) {
        List<Swipe> likes = swipeRepository.findPendingIncomingLikes(userId, SwipeDirection.LIKE);
        return likes.stream()
                .map(swipe -> {
                    User liker = userService.getById(swipe.getSwiperId());
                    return new IncomingLike(
                        liker.getId(), liker.getName(), liker.getAge(), liker.getBio(),
                        liker.getWorkoutGoal(), liker.getGymName(), liker.getCity(),
                        swipe.getCreatedAt());
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public long countIncomingLikes(Long userId) {
        return swipeRepository.findPendingIncomingLikes(userId, SwipeDirection.LIKE).size();
    }
}
