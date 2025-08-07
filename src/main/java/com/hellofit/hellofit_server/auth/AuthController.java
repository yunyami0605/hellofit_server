package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.LoginRequestDto;
import com.hellofit.hellofit_server.auth.dto.LoginResponseDto;
import com.hellofit.hellofit_server.auth.dto.SignupRequestDto;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MutationResponse> signup(@RequestBody @Valid SignupRequestDto request) {
        return ResponseEntity.ok(
                new MutationResponse(authService.signup(request))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
