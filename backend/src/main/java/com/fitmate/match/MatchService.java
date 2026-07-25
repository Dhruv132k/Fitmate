package com.fitmate.match;

import com.fitmate.match.dto.MatchResponse;
import com.fitmate.user.User;
import com.fitmate.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserService userService;

    public MatchService(MatchRepository matchRepository, UserService userService) {
        this.matchRepository = matchRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatches(Long userId) {
        return matchRepository.findAllForUser(userId).stream()
                .map(match -> {
                    Long otherId = match.getUserAId().equals(userId)
                            ? match.getUserBId() : match.getUserAId();
                    User other = userService.getById(otherId);
                    return new MatchResponse(
                            match.getId(), other.getId(), other.getName(), other.getAge(),
                            other.getBio(), other.getWorkoutGoal(), other.getGymName(),
                            other.getCity(), match.getCreatedAt());
                })
                .toList();
    }
}
