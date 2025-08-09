package com.hellofit.hellofit_server.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/*
 * Cors 설정 설정
 * */
@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 개발용, 모든 엔드포인트, 출처에 CORS 허용
        registry.addMapping("/**")
                .allowedOrigins("*")  // 테스트 시 전체 허용 (배포 땐 도메인 지정 권장)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}