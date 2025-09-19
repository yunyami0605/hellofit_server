package com.hellofit.hellofit_server.post;

import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.post.dto.PostRequestDto;
import com.hellofit.hellofit_server.post.dto.PostResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    @ResponseStatus(HttpStatus.CREATED)
    public MutationResponse createPost(
        @Valid @RequestBody PostRequestDto.Create request,
        @AuthenticationPrincipal UUID userId
    ) {
        return this.postService.createPost(request, userId);
    }

    @Operation(summary = "게시글 수정 API")
    @ApiResponse(
        responseCode = "200",
        description = "게시글 수정 성공"
    )
    @PatchMapping("/{id}")
    public MutationResponse patchPost(@AuthenticationPrincipal UUID userId, @PathVariable UUID id, @Valid @RequestBody PostRequestDto.Update request) {
        return postService.updatePost(userId, id, request);
    }

    @Operation(summary = "유저가 작성한 게시글 목록 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "유저가 작성한 게시글 목록 조회 성공"
    )
    @GetMapping("/user")
    public CursorResponse<PostResponseDto.SummaryList> getPostsByUser(
        @AuthenticationPrincipal UUID userId,
        @RequestParam(required = false) LocalDateTime cursorId,
        @RequestParam(defaultValue = "10") int size
    ) {
        return postService.getPostsByUser(userId, cursorId, size);
    }

    @Operation(summary = "게시글 목록 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "게시글 목록 조회 성공"
    )
    @GetMapping
    public CursorResponse<PostResponseDto.SummaryList> getPosts(
        @RequestParam(required = false) LocalDateTime cursorId,
        @RequestParam(defaultValue = "10") int size
    ) {
        return postService.getPosts(cursorId, size);
    }

    @Operation(summary = "게시글 검색 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "게시글 검색 조회 성공"
    )
    @GetMapping("/search")
    public List<PostResponseDto.Summary> getSearchPosts(@RequestParam(required = false) String keyword) {
        return postService.getSearchPosts(keyword);
    }

    @Operation(summary = "게시글 하나 조회 API")
    @GetMapping("/{id}")
    public PostResponseDto.Summary getPostOne(@PathVariable UUID id) {
        return postService.getPostOne(id);
    }

    @Operation(summary = "게시글 수정 데이터 조회 API")
    @GetMapping("/patch/{id}")
    public PostResponseDto.PatchData getPatchPostOne(@PathVariable UUID id) {
        return postService.getPatchPostOne(id);
    }

    @Operation(summary = "게시글 삭제 API")
    @DeleteMapping("/{id}")
    public MutationResponse deletePost(@AuthenticationPrincipal UUID userId, @PathVariable UUID id) {
        return postService.deletePostOne(userId, id);
    }

}
