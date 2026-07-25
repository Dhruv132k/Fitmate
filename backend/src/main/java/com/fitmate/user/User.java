package com.fitmate.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_gym_name", columnList = "gymName"),
        @Index(name = "idx_users_city", columnList = "city")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @ColumnDefault("'LOCAL'")
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

    /*Null for LOCAL */
    private String providerId;

    @Column(nullable = false)
    private String name;

    private Integer age;

    @Column(length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutGoal workoutGoal;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    /** Name of the gym the user trains at (used for matching). */
    private String gymName;

    /** City / workout location (used for matching). */
    private String city;

    private Double latitude;

    private Double longitude;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        if (this.workoutGoal == null) {
            this.workoutGoal = WorkoutGoal.GENERAL_FITNESS;
        }
        if(this.provider == null) {
            this.provider = AuthProvider.LOCAL;
        }
    }
}
