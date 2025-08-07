package com.hellofit.hellofit_server.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // 이메일 중복 체크
    Optional<User> findByEmail(String email);
}
