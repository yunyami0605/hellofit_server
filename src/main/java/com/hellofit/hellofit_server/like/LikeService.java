package com.hellofit.hellofit_server.like;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 좋아요 서비스 로직
     *
     * @param userId 좋아요 누른 유저
     * @param postId 타겟 게시글
     */
    @Transactional
    public boolean togglePostLike(UUID userId, UUID postId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Optional<LikeEntity> existing = likeRepository.findByUserAndTargetTypeAndTargetId(user, LikeTargetType.POST, postId);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get()); // 좋아요 취소

            return false;
        } else {
            LikeEntity like = LikeEntity.builder()
                .user(user)
                .targetType(LikeTargetType.POST)
                .targetId(postId)
                .build();
            likeRepository.save(like); // 좋아요 추가

            return true;
        }
    }

    public int getLikeCount(UUID postId, LikeTargetType type) {
        return likeRepository.countByTargetTypeAndTargetId(type, postId);
    }
}
