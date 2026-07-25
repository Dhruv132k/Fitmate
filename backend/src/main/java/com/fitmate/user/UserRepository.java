package com.fitmate.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    /**
     * Candidate feed: everyone except the current user and anyone the current
     * user has already swiped on. Ranked so that shared workout goal, same gym,
     * and same city surface first.
     */
    @Query("""
            SELECT u FROM User u
            WHERE u.id <> :userId
              AND u.active = true
              AND u.id NOT IN (
                  SELECT s.targetId FROM Swipe s WHERE s.swiperId = :userId
              )
            ORDER BY
              CASE WHEN u.workoutGoal = :goal THEN 0 ELSE 1 END,
              CASE WHEN u.gymName = :gymName THEN 0 ELSE 1 END,
              CASE WHEN u.city = :city THEN 0 ELSE 1 END,
              u.id DESC
            """)
    List<User> findCandidates(@Param("userId") Long userId,
                              @Param("goal") WorkoutGoal goal,
                              @Param("gymName") String gymName,
                              @Param("city") String city);
}
