package com.fitmate.discovery;

import com.fitmate.discovery.dto.CandidateCard;
import com.fitmate.user.ExperienceLevel;
import com.fitmate.user.User;
import com.fitmate.user.UserRepository;
import com.fitmate.user.UserService;
import com.fitmate.user.WorkoutGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscoveryServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserService userService;

    @InjectMocks DiscoveryService discoveryService;

    private User me() {
        return User.builder().id(1L).name("Me").workoutGoal(WorkoutGoal.MUSCLE_GAIN)
                .experienceLevel(ExperienceLevel.INTERMEDIATE).gymName("Iron Paradise").city("Pune").build();
    }

    @Test
    void perfectMatchGetsHighScoreAndAllReasons() {
        User me = me();
        User other = User.builder().id(2L).name("Alex").workoutGoal(WorkoutGoal.MUSCLE_GAIN)
                .experienceLevel(ExperienceLevel.INTERMEDIATE).gymName("Iron Paradise").city("Pune").build();
        when(userService.getById(1L)).thenReturn(me);
        when(userRepository.findCandidates(any(), any(), any(), any())).thenReturn(List.of(other));

        List<CandidateCard> feed = discoveryService.getFeed(1L).getCandidates();

        assertThat(feed).hasSize(1);
        CandidateCard card = feed.get(0);
        assertThat(card.compatibilityScore()).isEqualTo(100);
        assertThat(card.matchReasons()).hasSize(3);
    }

    @Test
    void unrelatedCandidateScoresZeroWithNoReasons() {
        User me = me();
        User other = User.builder().id(3L).name("Zed").workoutGoal(WorkoutGoal.ENDURANCE)
                .experienceLevel(ExperienceLevel.ADVANCED).gymName("Other Gym").city("Delhi").build();
        when(userService.getById(1L)).thenReturn(me);
        when(userRepository.findCandidates(any(), any(), any(), any())).thenReturn(List.of(other));

        CandidateCard card = discoveryService.getFeed(1L).getCandidates().get(0);

        assertThat(card.compatibilityScore()).isZero();
        assertThat(card.matchReasons()).isEmpty();
    }

    @Test
    void feedIsCappedAtThirty() {
        User me = me();
        List<User> many = java.util.stream.IntStream.rangeClosed(1, 50)
                .mapToObj(i -> User.builder().id((long) (i + 10)).name("U" + i)
                        .workoutGoal(WorkoutGoal.STRENGTH).build())
                .toList();
        when(userService.getById(1L)).thenReturn(me);
        when(userRepository.findCandidates(any(), any(), any(), any())).thenReturn(many);

        assertThat(discoveryService.getFeed(1L).getCandidates()).hasSize(30);
    }
}
