package com.hellofit.hellofit_server.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
}
