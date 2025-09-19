package com.hellofit.hellofit_server.post;

import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.post.dto.PostRequestDto;
import com.hellofit.hellofit_server.post.dto.PostResponseDto;
import com.hellofit.hellofit_server.user.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 API", description = "게시글 CRUD API")
@SecurityRequirement(name = "bearerAuth")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시글 생성 API")
    @ApiResponse(
        responseCode = "201",
        description = "게시글 생성 성공"
    )
    @PostMapping
    public ResponseEntity<MutationResponse> createPost(
        @Valid @RequestBody PostRequestDto.Create request,
        @AuthenticationPrincipal UserEntity user
    ) {
        return ResponseEntity.status(201)
            .body(this.postService.createPost(request, user));
    }

    @Operation(summary = "게시글 수정 API")
    @ApiResponse(
        responseCode = "200",
        description = "게시글 수정 성공"
    )
    @PutMapping("/{id}")
    public ResponseEntity<MutationResponse> putPost(@AuthenticationPrincipal UserEntity user, @PathVariable UUID id, @Valid @RequestBody PostRequestDto.Update request) {
        // updatePost
        MutationResponse post = postService.updatePost(user.getId(), id, request);
        return ResponseEntity.ok(post);
    }

    @Operation(summary = "유저가 작성한 게시글 목록 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "유저가 작성한 게시글 목록 조회 성공"
    )
    @GetMapping("/user")
    public ResponseEntity<CursorResponse<PostResponseDto.SummaryList>> getPostsByUser(
        @AuthenticationPrincipal UserEntity user,
        @RequestParam(required = false) LocalDateTime cursorId,
        @RequestParam(defaultValue = "10") int size
    ) {
        CursorResponse<PostResponseDto.SummaryList> posts = postService.getPostsByUser(user, cursorId, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 목록 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "게시글 목록 조회 성공"
    )
    @GetMapping
    public ResponseEntity<CursorResponse<PostResponseDto.SummaryList>> getPosts(
        @RequestParam(required = false) LocalDateTime cursorId,
        @RequestParam(defaultValue = "10") int size
    ) {
        CursorResponse<PostResponseDto.SummaryList> posts = postService.getPosts(cursorId, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 검색 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "게시글 검색 조회 성공"
    )
    @GetMapping("/search")
    public ResponseEntity<List<PostResponseDto.Summary>> getSearchPosts(@RequestParam(required = false) String keyword) {
        List<PostResponseDto.Summary> posts = postService.getSearchPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 하나 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto.Summary> getPostOne(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPostOne(id));
    }

    @Operation(summary = "게시글 수정 데이터 조회 API")
    @GetMapping("/patch/{id}")
    public ResponseEntity<PostResponseDto.PatchData> getPatchPostOne(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPatchPostOne(id));
    }

    @Operation(summary = "게시글 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<MutationResponse> deletePost(@AuthenticationPrincipal UserEntity user, @PathVariable UUID id) {
        return ResponseEntity.ok(postService.deletePostOne(user.getId(), id));
    }

}
