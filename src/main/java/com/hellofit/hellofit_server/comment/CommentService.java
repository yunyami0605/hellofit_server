package com.hellofit.hellofit_server.comment;

import com.hellofit.hellofit_server.comment.dto.CommentResponseDto;
import com.hellofit.hellofit_server.comment.exception.CommentException;
import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.global.exception.CommonException;
import com.hellofit.hellofit_server.like.LikeRepository;
import com.hellofit.hellofit_server.like.LikeTargetType;
import com.hellofit.hellofit_server.post.PostEntity;
import com.hellofit.hellofit_server.post.PostRepository;
import com.hellofit.hellofit_server.post.exception.PostException;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 댓글만 조회 (커서방식)
     */
    public CursorResponse<CommentResponseDto.Summary> getTopLevelCommentsWithCursor(UUID postId, LocalDateTime cursorId, int size) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new PostException.NotFound("getTopLevelCommentsWithCursor", postId.toString()));

        Pageable pageable = PageRequest.of(0, size + 1);

        // 1. 댓글 조회
        List<CommentEntity> comments = commentRepository.getTopLevelCommentsWithCursor(post, cursorId, pageable);

        // 2. 다음 페이지 존재 여부 체크
        boolean hasNext = comments.size() > size;

        // 3. 필요한 개수만큼 자르기
        List<CommentEntity> resizedComments = hasNext ? comments.subList(0, size) : comments;

        // 4.각 댓글에 좋아요 카운트 필드 추가
        List<CommentResponseDto.Summary> items = resizedComments.stream()
            .map((_comment) -> {
                int likeCount = this.likeRepository.countByTargetTypeAndTargetId(LikeTargetType.COMMENT, _comment.getId());

                return CommentResponseDto.Summary.from(_comment, likeCount);
            })
            .toList();

        // 5. nextCursor 설정
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
     * 특정 댓글에 대한 답글 조회 (커서방식
     */
    public CursorResponse<CommentResponseDto.Summary> getRecomments(UUID parentId, LocalDateTime cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<CommentEntity> recomments = commentRepository.findRecommentsWithCursor(parentId, cursorId, pageable);


        boolean hasNext = recomments.size() > size;

        List<CommentEntity> resizedRecomments = hasNext ? recomments.subList(0, size) : recomments;


        List<CommentResponseDto.Summary> items = recomments.stream()
            .map((_comment) -> {
                int likeCount = likeRepository.countByTargetTypeAndTargetId(LikeTargetType.COMMENT, _comment.getId());
                return CommentResponseDto.Summary.from(_comment, likeCount);
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

        List<CommentResponseDto.Summary> items = resizedComments.stream()
            .map((_comment) -> {
                int likeCount = this.likeRepository.countByTargetTypeAndTargetId(LikeTargetType.COMMENT, _comment.getId());

                return CommentResponseDto.Summary.from(_comment, likeCount);
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
     * 댓글 생성
     */
    @Transactional
    public MutationResponse createComment(UUID postId, UserEntity user, String content) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new PostException.NotFound("createComment", postId.toString()));

        CommentEntity comment = CommentEntity.create(post, user, content, null);

        CommentEntity savedComment = commentRepository.save(comment);

        return MutationResponse.builder()
            .id(savedComment.getId())
            .build();
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public MutationResponse updateComment(UUID commentId, String content, UUID userId) {
        // 1. 댓글 조회
        CommentEntity comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException.NotFound("updateComment", commentId.toString()));

        log.info("test {} {}", comment.getUser()
            .getId(), userId);

        // 2. 유저 확인
        if (!comment.getUser()
            .getId()
            .equals(userId)) {
            throw new CommonException.Forbidden("updateComment", userId.toString());
        }

        // 3. 댓글 수정
        comment.changeContent(content);

        return MutationResponse.builder()
            .id(commentId)
            .build();
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public MutationResponse deleteComment(UUID commentId, UUID userId) {
        // 1. 댓글 조회
        CommentEntity comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException.NotFound("deleteComment", commentId.toString()));

        // 2. 유저 확인
        if (!comment.getUser()
            .getId()
            .equals(userId)) {
            throw new CommonException.Forbidden("updateComment", userId.toString());
        }

        // 3. 댓글 soft delete
        comment.softDelete();

        return MutationResponse.builder()
            .id(commentId)
            .build();
    }

    /**
     * 답글 생성
     */
    @Transactional
    public MutationResponse createRecomment(UUID postId, UUID userId, String content, UUID parentId) {
        CommentEntity parent = commentRepository.findById(parentId)
            .orElseThrow(() -> new CommentException.NotFound("createRecomment", parentId.toString()));

        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new PostException.NotFound("createRecomment", postId.toString()));

        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new PostException.NotFound("createRecomment", postId.toString()));

        CommentEntity recomment = CommentEntity.create(post, user, content, parent);
        parent.addRecomment(recomment);

        CommentEntity savedRecomment = commentRepository.save(recomment);

        return MutationResponse.builder()
            .id(savedRecomment.getId())
            .build();
    }
}
