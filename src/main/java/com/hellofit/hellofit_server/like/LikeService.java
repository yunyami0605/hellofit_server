package com.hellofit.hellofit_server.like;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;

    /**
     * 게시글 좋아요 서비스 로직
     *
     * @param userId     좋아요 누른 유저
     * @param targetType 타겟 유형
     * @param targetId   타겟 id
     */
    @Transactional
    public boolean togglePostLike(UUID userId, LikeTargetType targetType, UUID targetId) {
        UserEntity user = userService.getUserById(userId, "LikeService > togglePostLike");

        Optional<LikeEntity> existing = likeRepository.findByUserAndTargetTypeAndTargetId(user, targetType, targetId);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get()); // 좋아요 취소

            return false;
        } else {
            LikeEntity like = LikeEntity.create(user, targetType, targetId);

            likeRepository.save(like); // 좋아요 추가

            return true;
        }
    }

    /**
     * 좋아요 갯수 조회
     */
    public int getLikeCount(UUID targetId, LikeTargetType type) {
        return likeRepository.countByTargetTypeAndTargetId(type, targetId);
    }
}
