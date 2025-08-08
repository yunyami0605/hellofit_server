package com.hellofit.hellofit_server.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    // 저장 또는 업데이트
    public void saveOrUpdate(UUID userId, String refreshToken) {
        refreshTokenRepository.findById(userId)
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(userId)
                                        .token(refreshToken)
                                        .build()
                        )
                );
    }

    // 토큰 조회
    public Optional<RefreshToken> findByUserId(UUID userId){
        return refreshTokenRepository.findById(userId);
    }

    // 토큰 삭제
    public void deleteByUserId(UUID userId){
        refreshTokenRepository.deleteById(userId);
    }
}
