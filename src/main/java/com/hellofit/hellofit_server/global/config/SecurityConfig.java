package com.hellofit.hellofit_server.global.config;

import com.hellofit.hellofit_server.global.jwt.JwtAuthenticationFilter;
import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import com.hellofit.hellofit_server.global.security.PepperingPasswordEncoder;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    // 401을 즉시 반환할 엔트리포인트
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    // JWT 검증 필터
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationEntryPoint unauthorizedEntryPoint) {
        return new JwtAuthenticationFilter(jwtTokenProvider, unauthorizedEntryPoint);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }


    @Bean
    public PasswordEncoder passwordEncoder(@Value("${hellofit.security.pepper}") String pepper) {
        int strength = 12;
        return new PepperingPasswordEncoder(new BCryptPasswordEncoder(strength), pepper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationEntryPoint unauthorizedEntryPoint,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // withDefaults() 동일
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT는 무상태
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedEntryPoint))       // 전역 401 처리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/h2-console/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(fl -> fl.disable())   // 폼 로그인 사용 안함
                .httpBasic(hb -> hb.disable())   // 기본 인증 사용 안함
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 등록
                .build();
    }

    // ⛔️ InMemoryUserDetailsService는 httpBasic/formLogin을 쓸 때만 필요.
    // JWT만 쓸 거면 제거하거나 테스트 용도로만 유지하세요.
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails user = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("admin1234"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }
}
