package com.hellofit.hellofit_server.user.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, UUID> {
    boolean existsByUserId(UUID userId);
}
