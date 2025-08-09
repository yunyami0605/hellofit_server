package com.hellofit.hellofit_server.global.config;

import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import com.hellofit.hellofit_server.global.security.PepperingPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/*
* Spring Security 설정
* */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    // 필드 추가
    private final JwtTokenProvider jwtTokenProvider;

    // 비밀번호 인코딩 메서드 제공하는 빈객체 등록
    @Bean
    public PasswordEncoder passwordEncoder(
            @Value("${hellofit.security.pepper}") String pepper
    ) {
        int strength = 12; // 서버 성능보고 조절
        return new PepperingPasswordEncoder(new BCryptPasswordEncoder(strength), pepper);
    }

    // swagger 보안 필터
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (테스트 목적)
                .csrf().disable() // CSRF 비활성화
                .cors(Customizer.withDefaults()) // 👈 이 줄 추가!
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/auth/**"        // 로그인, 회원가입 등 인증 없이 허용
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
//                .httpBasic(Customizer.withDefaults()); // 기본 로그인 창 사용 (테스트용)

        return http.build();
    }

    // 테스트용 사용자 등록 (인메모리)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin1234"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
