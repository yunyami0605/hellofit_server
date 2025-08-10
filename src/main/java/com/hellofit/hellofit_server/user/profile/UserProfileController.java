package com.hellofit.hellofit_server.user.profile;

import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.user.profile.dto.CreateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UpdateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping("/users/{userId}/profile")
    @ResponseStatus(HttpStatus.CREATED)
    public MutationResponse createProfile(
            @PathVariable UUID userId,
            @RequestBody @Valid CreateUserProfileRequestDto request
            ){
        return new MutationResponse(this.userProfileService.createProfile(userId, request));
    }

    @GetMapping("/users/{userId}/profile")
    public UserProfileResponse getProfile(@PathVariable UUID userId) {
        return this.userProfileService.getProfileById(userId);
    }

    @PatchMapping("/users/{userId}/profile")
    public MutationResponse patchProfile(@PathVariable UUID userId, @RequestBody @Valid UpdateUserProfileRequestDto request){
        return new MutationResponse(this.userProfileService.patchProfile(userId, request));
    }
}
