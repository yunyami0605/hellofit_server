package com.hellofit.hellofit_server.global.jwt;

import com.hellofit.hellofit_server.auth.constants.TokenStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    public enum Role {USER, ADMIN}

    private final String secretKey = "SuperStrongSecretKeyForJWT"; // 직접 하드코딩
    private final long validityInMilliseconds = 36000000L;

    private Key key;

    private final long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60; // 1시간
    private final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getEncoder()
            .encode(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    private String createToken(UUID userId, Role role, Long expireTime) {
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("role", role.name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expireTime))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * ACCESS TOKEN 생성
     *
     * @param role : USER | ADMIN
     * @return token 값
     */
    public String generateAccessToken(UUID userId, Role role) {
        if (role == null) {
            role = Role.USER;
        }

        return this.createToken(userId, role, ACCESS_TOKEN_VALID_TIME);
    }

    /**
     * REFRESH TOKEN 생성
     *
     * @param role : USER | ADMIN
     * @return token 값
     */
    public String generateRefreshToken(UUID userId, Role role) {
        if (role == null) {
            role = Role.USER;
        }

        return this.createToken(userId, role, REFRESH_TOKEN_VALID_TIME);
    }

    /**
     * 유저 id 반환 from token
     */
    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(
            this.parseClaims(token)
                .getSubject()
        );
    }

    /**
     * 유저 role 반환 from token
     */
    @SuppressWarnings("unchecked")
    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * 토큰에서 claim 부분 반환
     *
     * @return 토큰 claim dict 값
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }


    public TokenStatus validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {

            return TokenStatus.INVALID;
        }
    }

    public String generateServiceToken() {
        return Jwts.builder()
            .setSubject("SERVICE") // userId 대신 SERVICE
            .claim("role", "SERVICE")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + (1000L * 60 * 60))) // 1시간
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
}
