package com.hellofit.hellofit_server.like;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "likes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"})
    }
)
@Getter
@NoArgsConstructor
public class LikeEntity extends BaseEntity {
    /**
     * 좋아요 누른 유저
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * 좋아요 대상 종류 (게시글 / 댓글 / 기타)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private LikeTargetType targetType;

    /**
     * 대상 PK (PostEntity.id, CommentEntity.id 등)
     */
    @Column(name = "target_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID targetId;


    public static LikeEntity create(UserEntity user, LikeTargetType type, UUID targetId) {
        LikeEntity likeEntity = new LikeEntity();

        likeEntity.user = user;
        likeEntity.targetType = type;
        likeEntity.targetId = targetId;

        return likeEntity;
    }
}
