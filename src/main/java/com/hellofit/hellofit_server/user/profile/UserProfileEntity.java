package com.hellofit.hellofit_server.user.profile;

import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_profiles")
public class UserProfileEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private UUID userId; // PK = FK

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "id",
        columnDefinition = "CHAR(36)",
        foreignKey = @ForeignKey(name = "fk_user_profile_user"))
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeGroup ageGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal height;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

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

    private Integer exerciseMinutes;

    public enum AgeGroup {AGE_10S, AGE_20S, AGE_30S, AGE_40S, AGE_50S, AGE_60S, AGE_70S, AGE_80S, AGE_90S}

    public enum Gender {MALE, FEMALE}

    public static UserProfileEntity create(UserEntity user, AgeGroup ageGroup, Gender gender, BigDecimal height, BigDecimal weight, Integer sleepMinutes, Integer exerciseMinutes, Set<String> forbiddenFoods) {
        if (user == null) throw new IllegalArgumentException("User is required");
        if (height == null || height.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Height must be positive");
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Weight must be positive");

        UserProfileEntity profileEntity = new UserProfileEntity();
        profileEntity.user = user;
        profileEntity.changeAgeGroup(ageGroup);
        profileEntity.changeGender(gender);
        profileEntity.changeHeight(height);
        profileEntity.changeWeight(weight);
        profileEntity.changeSleepMinutes(sleepMinutes);
        profileEntity.changeExerciseMinutes(exerciseMinutes);
        profileEntity.forbiddenFoods = (forbiddenFoods != null) ? new HashSet<>(forbiddenFoods) : new HashSet<>();

        return profileEntity;
    }

    public void changeExerciseMinutes(Integer minutes) {
        if (minutes != null && minutes < 0) {
            throw new IllegalArgumentException("Exercise minutes cannot be negative");
        }
        this.exerciseMinutes = minutes;
    }


    public void changeGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("Gender be null");
        }

        this.gender = gender;
    }

    public void changeAgeGroup(AgeGroup group) {
        if (group == null) {
            throw new IllegalArgumentException("Age group access be null");
        }

        this.ageGroup = group;
    }

    public void changeHeight(BigDecimal height) {
        if (height == null || height.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }

        this.height = height;
    }

    public void changeWeight(BigDecimal weight) {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }

        this.weight = weight;
    }

    public void changeSleepMinutes(Integer minutes) {
        if (minutes != null && minutes < 0) {
            throw new IllegalArgumentException("Sleep minutes cannot be negative");
        }
        this.sleepMinutes = minutes;
    }

    public void addForbiddenFood(String food) {
        if (food == null || food.isBlank()) {
            throw new IllegalArgumentException("Food name cannot be blank");
        }
        this.forbiddenFoods.add(food);
    }


    public void replaceForbiddenFoods(List<String> foods) {
        Set<String> normarlizedfoods = this.normalizeFoods(foods);
        this.forbiddenFoods.clear();
        normarlizedfoods.forEach(this::addForbiddenFood);
    }

    private Set<String> normalizeFoods(List<String> input) {
        if (input == null) return new HashSet<>();
        // 공백 트림, 빈 값 제거, 길이 제한, 중복 제거(Set)
        return input.stream()
            .map(s -> s == null ? null : s.trim()
                .toLowerCase())
            .filter(s -> s != null && !s.isEmpty())
            .map(s -> s.length() > 100 ? s.substring(0, 100) : s)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
