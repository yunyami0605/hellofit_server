package com.hellofit.hellofit_server.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "에러 응답 DTO")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "USER_NOT_FOUND")
    private String code;

    @Schema(description = "에러 메시지", example = "요청한 사용자를 찾을 수 없습니다.")
    private String message;
}
