package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.user.dto.CreateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UpdateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MutationResponse createUser(@RequestBody @Valid CreateUserRequestDto request) {
        return new MutationResponse(userService.createUser(request));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserMappingResponseDto.Summary getUserById(@PathVariable UUID id){
        return UserMappingResponseDto.Summary.fromEntity(userService.getUserById(id));
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserMappingResponseDto.Detail> getUsersByPage(Pageable pageable){
        return userService.getUsersByPage(pageable);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200 OK
    public MutationResponse updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequestDto request){
        return new MutationResponse(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200 OK
    public MutationResponse deleteUser(@PathVariable UUID id){

        UUID deletedId = userService.deleteUser(id);
        return new MutationResponse(deletedId);
    }
}
