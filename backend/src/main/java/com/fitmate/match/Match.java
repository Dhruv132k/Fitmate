package com.fitmate.match;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * A mutual like between two users. {@code userAId} is always the smaller id and
 * {@code userBId} the larger, so the pair is stored exactly once.
 */
@Entity
@Table(name = "matches", uniqueConstraints = {
        @UniqueConstraint(name = "uq_match_pair", columnNames = {"userAId", "userBId"})
}, indexes = {
        @Index(name = "idx_match_user_a", columnList = "userAId"),
        @Index(name = "idx_match_user_b", columnList = "userBId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userAId;

    @Column(nullable = false)
    private Long userBId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
