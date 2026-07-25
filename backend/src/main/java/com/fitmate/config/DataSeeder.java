package com.fitmate.config;

import com.fitmate.user.ExperienceLevel;
import com.fitmate.user.User;
import com.fitmate.user.UserRepository;
import com.fitmate.user.WorkoutGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds a set of demo profiles so the swipe deck is populated out of the box.
 * Enabled via {@code fitmate.seed.enabled=true} (on by default in docker/dev).
 * All demo accounts share the password {@code password123}.
 */
@Component
@ConditionalOnProperty(name = "fitmate.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String DEMO_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Users already present; skipping demo seed.");
            return;
        }
        String hash = passwordEncoder.encode(DEMO_PASSWORD);
        List<User> demo = List.of(
                demo("alex@fitmate.dev", "Alex", 27, WorkoutGoal.MUSCLE_GAIN, ExperienceLevel.INTERMEDIATE, "Iron Paradise", "Pune", "Push-pull-legs, evenings", hash),
                demo("bella@fitmate.dev", "Bella", 25, WorkoutGoal.WEIGHT_LOSS, ExperienceLevel.BEGINNER, "Gold's Gym", "Pune", "New to lifting, need a spotter", hash),
                demo("chris@fitmate.dev", "Chris", 30, WorkoutGoal.POWERLIFTING, ExperienceLevel.ADVANCED, "Iron Paradise", "Pune", "Meet prep, big three focus", hash),
                demo("divya@fitmate.dev", "Divya", 28, WorkoutGoal.MUSCLE_GAIN, ExperienceLevel.INTERMEDIATE, "Iron Paradise", "Pune", "Hypertrophy, 5 days/week", hash),
                demo("evan@fitmate.dev", "Evan", 24, WorkoutGoal.ENDURANCE, ExperienceLevel.INTERMEDIATE, "CrossFit Central", "Mumbai", "Runner cross-training", hash),
                demo("farah@fitmate.dev", "Farah", 26, WorkoutGoal.CROSSFIT, ExperienceLevel.ADVANCED, "CrossFit Central", "Mumbai", "WOD partner wanted", hash),
                demo("gaurav@fitmate.dev", "Gaurav", 31, WorkoutGoal.MUSCLE_GAIN, ExperienceLevel.ADVANCED, "Gold's Gym", "Pune", "Bodybuilding split", hash),
                demo("hina@fitmate.dev", "Hina", 23, WorkoutGoal.GENERAL_FITNESS, ExperienceLevel.BEGINNER, "Anytime Fitness", "Pune", "Just getting consistent", hash)
        );
        userRepository.saveAll(demo);
        log.info("Seeded {} demo users (password: {}).", demo.size(), DEMO_PASSWORD);
    }

    private User demo(String email, String name, int age, WorkoutGoal goal,
                      ExperienceLevel level, String gym, String city, String bio, String hash) {
        return User.builder()
                .email(email)
                .passwordHash(hash)
                .name(name)
                .age(age)
                .workoutGoal(goal)
                .experienceLevel(level)
                .gymName(gym)
                .city(city)
                .bio(bio)
                .active(true)
                .build();
    }
}
