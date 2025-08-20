package com.hellofit.hellofit_server.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    @Modifying
    @Query("delete from RefreshTokenEntity r where r.user.id = :userId")
    void deleteByUser_Id(UUID userId);

    Optional<RefreshTokenEntity> findByUser_Id(UUID userId);
}
