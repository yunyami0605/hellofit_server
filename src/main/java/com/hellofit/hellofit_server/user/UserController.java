package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.user.dto.CreateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UpdateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MutationResponse> createUser(@RequestBody @Valid CreateUserRequestDto request) {
        return ResponseEntity.ok(new MutationResponse(userService.createUser(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserMappingResponseDto.Summary> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(UserMappingResponseDto.Summary.fromEntity(userService.getUserById(id)));
    }

    @GetMapping("/list")
    public Page<UserMappingResponseDto.Detail> getUsersByPage(Pageable pageable){
        return userService.getUsersByPage(pageable);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MutationResponse> updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequestDto request){
        return ResponseEntity.ok(new MutationResponse(userService.updateUser(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MutationResponse> deleteUser(@PathVariable UUID id){

        UUID deletedId = userService.deleteUser(id);
        return ResponseEntity.ok(new MutationResponse(deletedId));
    }
}
