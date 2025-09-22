package com.hellofit.hellofit_server.aws.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AwsRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "s3 signed url 제공 api")
    public static class GetS3PresignedBody {
        @Schema(description = "업로드 파일 타입", example = "image/png")
        private String fileType;
    }
}
