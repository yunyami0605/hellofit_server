package com.hellofit.hellofit_server.comment;

import com.hellofit.hellofit_server.aws.AwsService;
import com.hellofit.hellofit_server.comment.dto.CommentRequestDto;
import com.hellofit.hellofit_server.comment.dto.CommentResponseDto;
import com.hellofit.hellofit_server.comment.exception.CommentException;
import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.global.exception.CommonException;
import com.hellofit.hellofit_server.image.ImageService;
import com.hellofit.hellofit_server.image.ImageTargetType;
import com.hellofit.hellofit_server.like.LikeRepository;
import com.hellofit.hellofit_server.like.LikeTargetType;
import com.hellofit.hellofit_server.post.PostEntity;
import com.hellofit.hellofit_server.post.PostService;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserService userService;
    private final ImageService imageService;
    private final AwsService awsService;

    /**
     * 댓글만 조회 (커서방식)
     */
    public CursorResponse<CommentResponseDto.Summary> getTopLevelCommentsWithCursor(UUID postId, LocalDateTime cursorId, int size) {
        PostEntity post = this.postService.getPostById(postId, "CommentService > getTopLevelCommentsWithCursor");

        Pageable pageable = PageRequest.of(0, size + 1);

        // 1. 댓글 조회
        List<CommentEntity> comments = commentRepository.getTopLevelCommentsWithCursor(post, cursorId, pageable);

        // 2. 다음 페이지 존재 여부 체크
        boolean hasNext = comments.size() > size;

        // 3. 필요한 개수만큼 자르기
        List<CommentEntity> resizedComments = hasNext ? comments.subList(0, size) : comments;

        // 4. 댓글 id 리스트로 좋아요 카운트 조회
        Map<UUID, Integer> likeCounts = getLikeCountsForComments(resizedComments);

        // 5. DTO 변환
        List<CommentResponseDto.Summary> items = resizedComments.stream()
            .map(_comment -> {
                List<String> _authorImages = imageService.getImages(ImageTargetType.UserProfile, _comment.getUser()
                        .getId())
                    .stream()
                    .map((v) -> {
                        return awsService.presignedGetUrl(v.getObjectKey());
                    })
                    .toList();

                String authorImage = _authorImages.isEmpty() ? null : _authorImages.get(0);

                return CommentResponseDto.Summary.from(_comment, likeCounts.getOrDefault(_comment.getId(), 0), authorImage);
            })
            .toList();

        // 6. nextCursor 설정
        String nextCursor = items.isEmpty() ? null : items.get(items.size() - 1)
            .getCreatedAt()
            .toString();

        return CursorResponse.<CommentResponseDto.Summary>builder()
            .items(items)
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .build();
    }

    /**
     * 특정 댓글에 대한 답글 조회 (커서방식)
     */
    public CursorResponse<CommentResponseDto.Summary> getRecomments(UUID parentId, LocalDateTime cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<CommentEntity> recomments = commentRepository.findRecommentsWithCursor(parentId, cursorId, pageable);

        boolean hasNext = recomments.size() > size;
        List<CommentEntity> resizedRecomments = hasNext ? recomments.subList(0, size) : recomments;

        // 좋아요 카운트 조회
        Map<UUID, Integer> likeCounts = getLikeCountsForComments(resizedRecomments);

        List<CommentResponseDto.Summary> items = resizedRecomments.stream()
            .map(_comment -> {
                List<String> _authorImages = imageService.getImages(ImageTargetType.UserProfile, _comment.getUser()
                        .getId())
                    .stream()
                    .map((v) -> {
                        return awsService.presignedGetUrl(v.getObjectKey());
                    })
                    .toList();

                String authorImage = _authorImages.isEmpty() ? null : _authorImages.get(0);

                return CommentResponseDto.Summary.from(_comment, likeCounts.getOrDefault(_comment.getId(), 0), authorImage);

            })
            .toList();

        String nextCursor = items.isEmpty() ? null : items.get(items.size() - 1)
            .getCreatedAt()
            .toString();

        return CursorResponse.<CommentResponseDto.Summary>builder()
            .items(items)
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .build();
    }

    /**
     * 유저의 댓글 조회 (커서방식)
     */
    public CursorResponse<CommentResponseDto.Summary> getUserCommentWithCursor(UUID userId, LocalDateTime cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<CommentEntity> comments = this.commentRepository.findByUserIdWithCursor(userId, cursorId, pageable);

        boolean hasNext = comments.size() > size;
        List<CommentEntity> resizedComments = hasNext ? comments.subList(0, size) : comments;

        // 좋아요 카운트 조회
        Map<UUID, Integer> likeCounts = getLikeCountsForComments(resizedComments);

        List<CommentResponseDto.Summary> items = resizedComments.stream()
            .map(_comment -> {
                List<String> _authorImages = imageService.getImages(ImageTargetType.UserProfile, _comment.getUser()
                        .getId())
                    .stream()
                    .map((v) -> {
                        return awsService.presignedGetUrl(v.getObjectKey());
                    })
                    .toList();

                String authorImage = _authorImages.isEmpty() ? null : _authorImages.get(0);
                return CommentResponseDto.Summary.from(_comment, likeCounts.getOrDefault(_comment.getId(), 0), authorImage);
            })
            .toList();

        String nextCursor = items.isEmpty() ? null : items.get(items.size() - 1)
            .getCreatedAt()
            .toString();

        return CursorResponse.<CommentResponseDto.Summary>builder()
            .items(items)
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .build();
    }

    /**
     * 댓글/답글 생성
     */
    @Transactional
    public MutationResponse createComment(UUID postId, UUID userId, CommentRequestDto.Create request) {
        UserEntity userEntity = this.userService.getUserById(userId, "CommentService > createComment");
        PostEntity postEntity = this.postService.getPostById(postId, "CommentService > createComment");

        UUID parentId = request.getParentId();
        UUID targetId = request.getTargetId();

        CommentEntity parentCommentEntity = null;
        CommentEntity targetCommentEntity = null;

        // 1. parentId가 있을 경우 → 부모 댓글 조회
        if (parentId != null) {
            parentCommentEntity = this.commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException.NotFound("부모 댓글을 찾을 수 없음", parentId));
        }

        // 2. targetId가 있을 경우 → 대상 댓글 조회
        if (targetId != null) {
            targetCommentEntity = this.commentRepository.findById(targetId)
                .orElseThrow(() -> new CommentException.NotFound("대상 댓글을 찾을 수 없음", targetId));
        }

        // 3. 검증 로직
        if (parentId == null && targetId != null) {
            throw new CommonException.BadRequest("CommentService > createComment", "잘못된 요청: parentId 없이 %s 존재할 수 없음", targetId.toString());
        }

        CommentEntity comment = CommentEntity.create(postEntity, userEntity, request.getContent(), parentCommentEntity, targetCommentEntity);
        commentRepository.save(comment);

        return MutationResponse.of(true);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public MutationResponse updateComment(UUID commentId, String content, UUID userId) {
        CommentEntity comment = this.getFindById(commentId, "CommentService > updateComment");

        if (!comment.getUser()
            .getId()
            .equals(userId)) {
            throw new CommonException.Forbidden("updateComment", userId.toString());
        }

        comment.changeContent(content);

        return MutationResponse.of(true);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public MutationResponse deleteComment(UUID commentId, UUID userId) {
        CommentEntity comment = this.getFindById(commentId, "CommentService > deleteComment");

        if (!comment.getUser()
            .getId()
            .equals(userId)) {
            throw new CommonException.Forbidden("deleteComment", userId.toString());
        }

        comment.softDelete();

        return MutationResponse.of(true);
    }

    /**
     * 헬퍼 메서드: 댓글 조회
     */
    public CommentEntity getFindById(UUID commentId, String errorMessage) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException.NotFound(errorMessage, commentId));
    }

    /**
     * 헬퍼 메서드: 댓글 리스트에 대한 좋아요 카운트 Map 조회
     */
    private Map<UUID, Integer> getLikeCountsForComments(List<CommentEntity> comments) {
        if (comments.isEmpty()) return Collections.emptyMap();

        List<UUID> ids = comments.stream()
            .map(CommentEntity::getId)
            .toList();

        // repository에 정의한 "댓글 여러 개에 대한 좋아요 수 조회" 메서드 사용
        List<Object[]> results = likeRepository.countByTargetIds(LikeTargetType.COMMENT, ids);

        return results.stream()
            .collect(Collectors.toMap(
                row -> (UUID) row[0],
                row -> ((Long) row[1]).intValue()
            ));
    }
}
