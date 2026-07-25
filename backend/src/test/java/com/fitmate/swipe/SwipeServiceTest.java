package com.fitmate.swipe;

import com.fitmate.common.BadRequestException;
import com.fitmate.common.ConflictException;
import com.fitmate.match.Match;
import com.fitmate.match.MatchRepository;
import com.fitmate.swipe.dto.SwipeRequest;
import com.fitmate.swipe.dto.SwipeResult;
import com.fitmate.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwipeServiceTest {

    @Mock SwipeRepository swipeRepository;
    @Mock MatchRepository matchRepository;
    @Mock UserRepository userRepository;

    @InjectMocks SwipeService swipeService;

    @BeforeEach
    void setUp() {
        lenient().when(userRepository.existsById(any())).thenReturn(true);
    }

    @Test
    void cannotSwipeOnSelf() {
        assertThatThrownBy(() -> swipeService.swipe(1L, new SwipeRequest(1L, SwipeDirection.LIKE)))
                .isInstanceOf(BadRequestException.class);
        verifyNoInteractions(swipeRepository);
    }

    @Test
    void rejectsSwipeOnNonExistentTarget() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> swipeService.swipe(1L, new SwipeRequest(99L, SwipeDirection.LIKE)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectsDuplicateSwipe() {
        when(swipeRepository.findBySwiperIdAndTargetId(1L, 2L))
                .thenReturn(Optional.of(new Swipe()));
        assertThatThrownBy(() -> swipeService.swipe(1L, new SwipeRequest(2L, SwipeDirection.LIKE)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void likeWithoutReciprocationDoesNotMatch() {
        when(swipeRepository.findBySwiperIdAndTargetId(1L, 2L)).thenReturn(Optional.empty());
        when(swipeRepository.existsBySwiperIdAndTargetIdAndDirection(2L, 1L, SwipeDirection.LIKE))
                .thenReturn(false);

        SwipeResult result = swipeService.swipe(1L, new SwipeRequest(2L, SwipeDirection.LIKE));

        assertThat(result.matched()).isFalse();
        verify(swipeRepository).save(any(Swipe.class));
        verify(matchRepository, never()).save(any());
    }

    @Test
    void passIsRecordedButNeverMatches() {
        when(swipeRepository.findBySwiperIdAndTargetId(1L, 2L)).thenReturn(Optional.empty());

        SwipeResult result = swipeService.swipe(1L, new SwipeRequest(2L, SwipeDirection.PASS));

        assertThat(result.matched()).isFalse();
        verify(swipeRepository).save(any(Swipe.class));
        verify(swipeRepository, never())
                .existsBySwiperIdAndTargetIdAndDirection(anyLong(), anyLong(), any());
    }

    @Test
    void mutualLikeCreatesMatchWithOrderedIds() {
        when(swipeRepository.findBySwiperIdAndTargetId(5L, 2L)).thenReturn(Optional.empty());
        when(swipeRepository.existsBySwiperIdAndTargetIdAndDirection(2L, 5L, SwipeDirection.LIKE))
                .thenReturn(true);
        when(matchRepository.findByUserAIdAndUserBId(2L, 5L)).thenReturn(Optional.empty());
        when(matchRepository.save(any(Match.class)))
                .thenAnswer(inv -> {
                    Match m = inv.getArgument(0);
                    m.setId(77L);
                    return m;
                });

        SwipeResult result = swipeService.swipe(5L, new SwipeRequest(2L, SwipeDirection.LIKE));

        assertThat(result.matched()).isTrue();
        assertThat(result.matchId()).isEqualTo(77L);

        ArgumentCaptor<Match> captor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository).save(captor.capture());
        assertThat(captor.getValue().getUserAId()).isEqualTo(2L);
        assertThat(captor.getValue().getUserBId()).isEqualTo(5L);
    }
}
