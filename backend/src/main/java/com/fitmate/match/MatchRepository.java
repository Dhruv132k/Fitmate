package com.fitmate.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByUserAIdAndUserBId(Long userAId, Long userBId);

    @Query("""
            SELECT m FROM Match m
            WHERE m.userAId = :userId OR m.userBId = :userId
            ORDER BY m.createdAt DESC
            """)
    List<Match> findAllForUser(@Param("userId") Long userId);
}
