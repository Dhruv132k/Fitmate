package com.fitmate.swipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {

    Optional<Swipe> findBySwiperIdAndTargetId(Long swiperId, Long targetId);

    boolean existsBySwiperIdAndTargetIdAndDirection(Long swiperId, Long targetId, SwipeDirection direction);
    
    @Query("""
    		SELECT s FROM Swipe s
    		WHERE s.targetId = :userId
    			AND s.direction = :direction
    			AND NOT EXISTS (
    				SELECT mine.id FROM Swipe mine
    				WHERE mine.swiperId = :userId AND mine.targetId = s.swiperId
    				)
    				ORDER BY s.createdAt DESC
    		""")
    List<Swipe> findPendingIncomingLikes (@Param("userId") Long userId,
    									  @Param("direction") SwipeDirection direction);
}
