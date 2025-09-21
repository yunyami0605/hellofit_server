package com.hellofit.hellofit_server.comment;

import com.hellofit.hellofit_server.comment.dto.CommentRequestDto;
import com.hellofit.hellofit_server.comment.dto.CommentResponseDto;
import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "댓글/답글 API", description = "댓글/답글 CRUD API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "게시글 댓글 조회 (cursor 방식)")
    @ApiResponse(responseCode = "200", description = "댓글 조회 성공")
    @GetMapping("/post/{postId}/comments")
    public CursorResponse<CommentResponseDto.Summary> getCommentsByPost(
        @PathVariable UUID postId,
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size
    ) {
        return commentService.getTopLevelCommentsWithCursor(postId, cursor, size);
    }

    /**
     * 특정 댓글의 답글 조회 (cursor 기반)
     */
    @Operation(summary = "특정 댓글의 답글 목록 조회 (cursor 방식)")
    @ApiResponse(responseCode = "200", description = "답글 목록 조회 성공")
    @GetMapping("/comments/{commentId}/recomments")
    public CursorResponse<CommentResponseDto.Summary> getRecomments(
        @PathVariable UUID commentId,
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size
    ) {
        return commentService.getRecomments(commentId, cursor, size);
    }


    /**
     * 유저의 댓글 조회 (cursor 기반)
     */
    @Operation(summary = "유저 댓글 조회 (cursor 방식)")
    @ApiResponse(responseCode = "200", description = "유저 댓글 조회 성공")
    @GetMapping("/comments/me")
    public CursorResponse<CommentResponseDto.Summary> getUserComments(
        @AuthenticationPrincipal UUID userId,
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size
    ) {
        return commentService.getUserCommentWithCursor(userId, cursor, size);
    }

    /**
     * 댓글/답글 생성
     */
    @Operation(summary = "댓글/답글 생성")
    @ApiResponse(responseCode = "200", description = "댓글/답글 생성 성공")
    @PostMapping("/posts/{postId}/comments")
    public MutationResponse createComment(
        @AuthenticationPrincipal UUID userId,
        @PathVariable UUID postId,
        @RequestBody @Valid CommentRequestDto.Create request
    ) {
        return commentService.createComment(postId, userId, request);
    }

    /**
     * 댓글/답글 수정
     */
    @Operation(summary = "댓글/답글 수정")
    @ApiResponse(responseCode = "200", description = "댓글/답글 수정 성공")
    @PatchMapping("/comments/{commentId}")
    public MutationResponse updateComment(
        @AuthenticationPrincipal UUID userId,
        @PathVariable UUID commentId,
        @RequestBody CommentRequestDto.Create request
    ) {
        return commentService.updateComment(commentId, request.getContent(), userId);
    }

    /**
     * 댓글/답글 삭제 (Soft Delete)
     */
    @Operation(summary = "댓글/답글 삭제")
    @ApiResponse(responseCode = "200", description = "댓글/답글 삭제 성공")
    @DeleteMapping("/comments/{commentId}")
    public MutationResponse deleteComment(
        @AuthenticationPrincipal UUID userId,
        @PathVariable UUID commentId) {
        return commentService.deleteComment(commentId, userId);
    }

}
