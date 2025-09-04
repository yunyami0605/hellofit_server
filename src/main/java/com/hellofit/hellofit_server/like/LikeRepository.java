package com.hellofit.hellofit_server.like;

import com.hellofit.hellofit_server.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {
    Optional<LikeEntity> findByUserAndTargetTypeAndTargetId(UserEntity user, LikeTargetType targetType, UUID targetId);

    int countByTargetTypeAndTargetId(LikeTargetType targetType, UUID targetId);
}
