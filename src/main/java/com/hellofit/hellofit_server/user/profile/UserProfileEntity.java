package com.hellofit.hellofit_server.user.profile;

import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "user_profiles")
public class UserProfileEntity  {

    @Id
    @Column(name = "user_id")
    private UUID userId; // PK = FK

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_user_profile_user"))
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeGroup ageGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Integer sleepMinutes;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_profile_forbidden_food",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    foreignKey = @ForeignKey(name = "fk_upf_user")
            )
    )
    @Column(name = "food", length = 100)

    private Set<String> forbiddenFoods = new HashSet<>();

    @Column(nullable = false)
    private Integer exerciseMinutes;

    public enum AgeGroup { AGE_10S, AGE_20S, AGE_30S, AGE_40S, AGE_50S, AGE_60S, AGE_70S, AGE_80S, AGE_90S }
    public enum Gender { MALE, FEMALE }
}
