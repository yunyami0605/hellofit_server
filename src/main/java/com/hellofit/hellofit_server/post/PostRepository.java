package com.hellofit.hellofit_server.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    /**
     * 제목에 특정 키워드가 포함된 게시글 검색
     */
    List<PostEntity> findByTitleContaining(String keyword);

    /**
     * 특정 유저가 작성한 게시글 목록 조회
     */
    List<PostEntity> findByUserId(UUID userId);

    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") UUID postId);
}
