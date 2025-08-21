package com.hellofit.hellofit_server.auth.dto;

import lombok.*;

public class AuthResponseDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 중복 체크 api response dto
    public static class NicknameDuplicate{
        private Boolean isDuplicate;
    }
}
