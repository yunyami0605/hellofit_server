package com.hellofit.hellofit_server.user.profile;

import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.user.profile.dto.CreateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UpdateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "유저 프로필 API", description = "유저 프로필 관련 API")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @Operation(
            summary = "유저 프로필 생성 API"
    )
    @ApiResponse(
            responseCode = "201",
            description = "생성 성공",
            content = @Content(schema = @Schema(implementation = MutationResponse.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "User already exists"
    )
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MutationResponse createProfile(
            @PathVariable UUID userId,
            @RequestBody @Valid CreateUserProfileRequestDto request
            ){
        return new MutationResponse(this.userProfileService.createProfile(userId, request));
    }

    @Operation(
            summary = "유저 프로필 조회 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @GetMapping("/{userId}")
    public UserProfileResponse getProfile(@PathVariable UUID userId) {
        return this.userProfileService.getProfileById(userId);
    }

    @Operation(
            summary = "유저 프로필 수정 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(implementation = MutationResponse.class))
    )
    @PatchMapping("/{userId}")
    public MutationResponse patchProfile(@PathVariable UUID userId, @RequestBody @Valid UpdateUserProfileRequestDto request){
        return new MutationResponse(this.userProfileService.patchProfile(userId, request));
    }
}
