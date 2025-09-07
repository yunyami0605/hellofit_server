package com.hellofit.hellofit_server.comment;

import com.hellofit.hellofit_server.post.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    // 특정 게시글 댓글 목록 커사방식으로 조회 (답글 제외)
    @Query("""
        SELECT c FROM CommentEntity c
        JOIN FETCH c.user
        WHERE c.post = :post
        AND c.parent IS NULL
        AND (:cursor IS NULL OR c.createdAt < :cursor)
        ORDER BY c.createdAt DESC
        """)
    List<CommentEntity> getTopLevelCommentsWithCursor(@Param("post") PostEntity post, @Param("cursor") LocalDateTime cursor, Pageable pageable);

    @Query("""
        SELECT c FROM CommentEntity c
        JOIN FETCH c.user
        WHERE c.parent.id = :parentId
        ORDER By c.createdAt ASC
        """)
    List<CommentEntity> findRecomments(@Param("parentId") UUID parentId, Pageable pageable);

    // 특정 유저 댓글 조회
    @Query("""
        SELECT c FROM CommentEntity c
        WHERE c.user.id = :userId AND c.parent IS NULL
        AND (:cursor IS NULL OR c.createdAt < :cursor)
        ORDER BY c.createdAt DESC
        """)
    List<CommentEntity> findByUserIdWithCursor(
        @Param("userId") UUID userId,
        @Param("cursor") LocalDateTime cursor,
        Pageable pageable
    );

    // 특정 댓글에 대한 답글 커서 조회
    @Query("""
        SELECT c FROM CommentEntity c
        JOIN FETCH c.user
        WHERE c.parent.id = :parentId
          AND (:cursor IS NULL OR c.createdAt < :cursor)
        ORDER BY c.createdAt DESC
        """)
    List<CommentEntity> findRecommentsWithCursor(
        @Param("parentId") UUID parentId,
        @Param("cursor") LocalDateTime cursor,
        Pageable pageable
    );

}
