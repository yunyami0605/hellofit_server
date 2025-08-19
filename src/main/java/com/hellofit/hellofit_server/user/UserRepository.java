package com.hellofit.hellofit_server.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    // 이메일 중복 체크
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByNickname(String nickname);
}
