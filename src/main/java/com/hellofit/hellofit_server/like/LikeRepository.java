package com.hellofit.hellofit_server.like;

import com.hellofit.hellofit_server.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {
    Optional<LikeEntity> findByUserAndTargetTypeAndTargetId(UserEntity user, LikeTargetType targetType, UUID targetId);

    Integer countByTargetTypeAndTargetId(LikeTargetType targetType, UUID targetId);

    @Query("""
        SELECT l.targetId, COUNT(l)
        FROM LikeEntity l
        WHERE l.targetType = :targetType
          AND l.targetId IN :targetIds
        GROUP BY l.targetId
        """)
    List<Object[]> countByTargetIds(
        @Param("targetType") LikeTargetType targetType,
        @Param("targetIds") List<UUID> targetIds
    );
}
