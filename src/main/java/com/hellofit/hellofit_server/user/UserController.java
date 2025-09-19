package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.global.constants.ErrorMessage;
import com.hellofit.hellofit_server.global.dto.ApiErrorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.global.dto.PageResponse;
import com.hellofit.hellofit_server.user.dto.CreateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UpdateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "유저 CRUD API")
@SecurityRequirement(name = "bearerAuth") // 기본은 인증 필요
public class UserController {
    private final UserService userService;

    @Operation(
        summary = "유저 생성 API"
    )
    @ApiResponse(
        responseCode = "201",
        description = "유저 생성 성공",
        content = @Content(schema = @Schema(implementation = MutationResponse.class))
    )
    @ApiResponse(responseCode = "409", description = ErrorMessage.DUPLICATE_EMAIL, content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MutationResponse createUser(@RequestBody @Valid CreateUserRequestDto request) {
        return userService.createUser(request);
    }

    @Operation(
        summary = "유저 조회 API"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "유저 조회 성공",
            content = @Content(schema = @Schema(implementation = UserMappingResponseDto.Summary.class))),
        @ApiResponse(responseCode = "404", description = ErrorMessage.USER_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserMappingResponseDto.Summary getUserById(@PathVariable UUID id) {
        return UserMappingResponseDto.Summary.fromEntity(userService.getUserById(id, "getUserById"));
    }

    @Operation(
        summary = "유저 리스트 조회 API"
    )
    @ApiResponse(responseCode = "200", description = "유저 리스트 조회 성공")
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<UserMappingResponseDto.Detail> getUsersByPage(
        @Parameter(description = "페이징 정보", example = "{ \"page\": 0, \"size\": 10 }")
        Pageable pageable
    ) {
        return userService.getUsersByPage(pageable);
    }

    @Operation(
        summary = "유저 수정 API"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "유저 수정 성공",
            content = @Content(schema = @Schema(implementation = MutationResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = ErrorMessage.USER_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200 OK
    public MutationResponse updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequestDto request) {
        return userService.updateUser(id, request);
    }

    @Operation(
        summary = "유저 삭제 API"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "유저 삭제 성공",
            content = @Content(schema = @Schema(implementation = MutationResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = ErrorMessage.USER_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200 OK
    public MutationResponse deleteUser(@PathVariable UUID id) {

        return userService.deleteUser(id);
    }
}
