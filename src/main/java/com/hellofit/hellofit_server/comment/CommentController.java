package com.hellofit.hellofit_server.comment;

import com.hellofit.hellofit_server.comment.dto.CommentResponseDto;
import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.post.PostRepository;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Operation(summary = "게시글 댓글 조회 (cursor 방식)")
    @ApiResponse(responseCode = "200", description = "댓글 조회 성공")
    @GetMapping("/post/{postId}")
    public ResponseEntity<CursorResponse<CommentResponseDto.Summary>> getCommentsByPost(
        @PathVariable UUID postId,
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(commentService.getTopLevelCommentsWithCursor(postId, cursor, size));
    }

    /**
     * 특정 댓글의 답글 조회 (cursor 기반)
     */
    @Operation(summary = "특정 댓글의 답글 목록 조회 (cursor 방식)")
    @ApiResponse(responseCode = "200", description = "답글 목록 조회 성공")
    @GetMapping("/{commentId}/recomments")
    public ResponseEntity<CursorResponse<CommentResponseDto.Summary>> getRecomments(
        @PathVariable UUID commentId,
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size
    ) {
        CursorResponse<CommentResponseDto.Summary> response = commentService.getRecomments(commentId, cursor, size);
        return ResponseEntity.ok(response);
    }


    /**
     * 유저의 댓글 조회 (cursor 기반)
     */
    @Operation(summary = "유저 댓글 조회 (cursor 방식)")
    @ApiResponse(responseCode = "200", description = "유저 댓글 조회 성공")
    @GetMapping("/user")
    public ResponseEntity<CursorResponse<CommentResponseDto.Summary>> getUserComments(
        @AuthenticationPrincipal UserEntity user,
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size
    ) {
        CursorResponse<CommentResponseDto.Summary> response =
            commentService.getUserCommentWithCursor(user.getId(), cursor, size);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 생성
     */
    @Operation(summary = "댓글 생성")
    @ApiResponse(responseCode = "200", description = "댓글 생성 성공")
    @PostMapping("/post/{postId}")
    public ResponseEntity<MutationResponse> createComment(
        @AuthenticationPrincipal UserEntity user,
        @PathVariable UUID postId,
        @RequestParam String content
    ) {
        MutationResponse response = commentService.createComment(postId, user, content);
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정
     */
    @Operation(summary = "댓글 수정")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공")
    @PatchMapping("/{commentId}")
    public ResponseEntity<MutationResponse> updateComment(
        @AuthenticationPrincipal UserEntity user,
        @PathVariable UUID commentId,
        @RequestParam String content
    ) {
        MutationResponse response = commentService.updateComment(commentId, content, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 삭제 (Soft Delete)
     */
    @Operation(summary = "댓글 삭제")
    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<MutationResponse> deleteComment(
        @AuthenticationPrincipal UserEntity user,
        @PathVariable UUID commentId) {
        MutationResponse response = commentService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 답글 생성
     */
    @Operation(summary = "답글 생성")
    @ApiResponse(responseCode = "200", description = "답글 생성 성공")
    @PostMapping("/{commentId}/recomments")
    public ResponseEntity<MutationResponse> createRecomment(
        @AuthenticationPrincipal UserEntity user,
        @PathVariable UUID commentId,
        @RequestParam UUID postId,
        @RequestParam String content
    ) {
        MutationResponse response = commentService.createRecomment(postId, user.getId(), content, commentId);
        return ResponseEntity.ok(response);
    }
}
