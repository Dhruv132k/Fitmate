package com.fitmate.swipe;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "swipes", uniqueConstraints = {
        @UniqueConstraint(name = "uq_swipe_swiper_target", columnNames = {"swiperId", "targetId"})
}, indexes = {
        @Index(name = "idx_swipe_swiper", columnList = "swiperId"),
        @Index(name = "idx_swipe_target", columnList = "targetId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long swiperId;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwipeDirection direction;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
