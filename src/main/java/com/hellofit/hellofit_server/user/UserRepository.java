package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.user.enums.LoginProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    // 이메일 중복 체크
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<UserEntity> findBySocialIdAndProvider(String socialId, LoginProvider loginProvider);
}
