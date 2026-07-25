package com.fitmate.match;

import com.fitmate.match.dto.MatchResponse;
import com.fitmate.user.User;
import com.fitmate.user.UserService;
import com.fitmate.user.WorkoutGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock MatchRepository matchRepository;
    @Mock UserService userService;

    @InjectMocks MatchService matchService;

    @Test
    void resolvesTheOtherUserRegardlessOfPairPosition() {
        Match m1 = Match.builder().id(1L).userAId(1L).userBId(2L).build();
        Match m2 = Match.builder().id(2L).userAId(3L).userBId(1L).build();
        when(matchRepository.findAllForUser(1L)).thenReturn(List.of(m1, m2));
        when(userService.getById(2L)).thenReturn(
                User.builder().id(2L).name("Bella").workoutGoal(WorkoutGoal.WEIGHT_LOSS).build());
        when(userService.getById(3L)).thenReturn(
                User.builder().id(3L).name("Chris").workoutGoal(WorkoutGoal.POWERLIFTING).build());

        List<MatchResponse> matches = matchService.getMatches(1L);

        assertThat(matches).extracting(MatchResponse::userId).containsExactly(2L, 3L);
        assertThat(matches).extracting(MatchResponse::name).containsExactly("Bella", "Chris");
    }
}
