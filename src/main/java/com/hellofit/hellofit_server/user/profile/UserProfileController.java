package com.hellofit.hellofit_server.user.profile;

import com.hellofit.hellofit_server.global.constants.ErrorMessage;
import com.hellofit.hellofit_server.global.dto.ApiErrorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import com.hellofit.hellofit_server.user.profile.dto.CreateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UpdateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UserProfileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        description = "유저 프로필 생성 성공",
        content = @Content(schema = @Schema(implementation = MutationResponse.class))
    )
    @ApiResponse(responseCode = "409", description = ErrorMessage.USER_PROFILE_DUPLICATE, content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public MutationResponse createProfile(
        @AuthenticationPrincipal UUID userId,
        @RequestBody @Valid CreateUserProfileRequestDto request
    ) {
        return this.userProfileService.createProfile(userId, request);
    }

    @Operation(
        summary = "유저 프로필 조회 API"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "유저 조회 성공",
            content = @Content(schema = @Schema(implementation = UserMappingResponseDto.Summary.class))),
        @ApiResponse(responseCode = "404", description = ErrorMessage.USER_PROFILE_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping()
    public UserProfileResponseDto.Detail getProfile(@AuthenticationPrincipal UUID userId) {
        return this.userProfileService.getProfileById(userId);
    }

    @Operation(
        summary = "유저 프로필 수정 API"
    )

    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponseDto.Detail.class))
        ),
        @ApiResponse(responseCode = "404", description = ErrorMessage.USER_PROFILE_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping()
    public UserProfileResponseDto.Detail patchProfile(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UpdateUserProfileRequestDto request) {
        return this.userProfileService.patchProfile(userId, request);
    }
}
