package com.hellofit.hellofit_server.like;

import com.hellofit.hellofit_server.user.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "좋아요 API", description = "좋아요 CRUD API")
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LikeController {
    private final LikeService likeService;

    /**
     * 게시글 좋아요 토글 on -> true, off -> false
     */
    @Operation(summary = "좋아요 TOGGLE API")
    @ApiResponse(
        responseCode = "200",
        description = "좋아요 on/off success"
    )
    @PostMapping("/toggle/{targetType}/{targetId}")
    public Boolean togglePostLike(
        @AuthenticationPrincipal UserEntity user,
        @PathVariable LikeTargetType targetType,
        @PathVariable UUID targetId
    ) {
        return likeService.togglePostLike(user.getId(), targetType, targetId);
    }

    @GetMapping("/{targetType}/{targetId}/count")
    public Integer getLikeCount(
        @PathVariable LikeTargetType targetType,
        @PathVariable UUID targetId
    ) {
        return likeService.getLikeCount(targetId, targetType);
    }
}

