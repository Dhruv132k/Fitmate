package com.fitmate.user;

import com.fitmate.swipe.Swipe;
import com.fitmate.swipe.SwipeDirection;
import com.fitmate.swipe.SwipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired SwipeRepository swipeRepository;

    private User newUser(String email, WorkoutGoal goal, String gym, String city) {
        return userRepository.save(User.builder()
                .email(email).passwordHash("h").name(email).workoutGoal(goal)
                .gymName(gym).city(city).active(true).build());
    }

    @Test
    void candidatesExcludeSelfAndAlreadySwiped() {
        User me = newUser("me@x.com", WorkoutGoal.MUSCLE_GAIN, "Iron", "Pune");
        User a = newUser("a@x.com", WorkoutGoal.MUSCLE_GAIN, "Iron", "Pune");
        User b = newUser("b@x.com", WorkoutGoal.ENDURANCE, "Other", "Delhi");
        User swiped = newUser("c@x.com", WorkoutGoal.MUSCLE_GAIN, "Iron", "Pune");

        swipeRepository.save(Swipe.builder()
                .swiperId(me.getId()).targetId(swiped.getId()).direction(SwipeDirection.PASS).build());

        List<User> candidates = userRepository.findCandidates(
                me.getId(), me.getWorkoutGoal(), me.getGymName(), me.getCity());

        assertThat(candidates).extracting(User::getId)
                .containsExactlyInAnyOrder(a.getId(), b.getId())
                .doesNotContain(me.getId(), swiped.getId());
    }

    @Test
    void candidatesRankSharedGoalGymAndCityFirst() {
        User me = newUser("me@x.com", WorkoutGoal.MUSCLE_GAIN, "Iron", "Pune");
        newUser("far@x.com", WorkoutGoal.ENDURANCE, "Other", "Delhi");
        User perfect = newUser("perfect@x.com", WorkoutGoal.MUSCLE_GAIN, "Iron", "Pune");

        List<User> candidates = userRepository.findCandidates(
                me.getId(), me.getWorkoutGoal(), me.getGymName(), me.getCity());

        assertThat(candidates).isNotEmpty();
        assertThat(candidates.get(0).getId()).isEqualTo(perfect.getId());
    }

    @Test
    void findByEmailIsCaseInsensitive() {
        newUser("Mixed@Case.com", WorkoutGoal.STRENGTH, "Iron", "Pune");
        assertThat(userRepository.findByEmailIgnoreCase("mixed@case.com")).isPresent();
        assertThat(userRepository.existsByEmailIgnoreCase("MIXED@CASE.COM")).isTrue();
    }
}
