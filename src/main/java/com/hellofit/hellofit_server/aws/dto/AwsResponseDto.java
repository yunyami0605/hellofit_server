package com.hellofit.hellofit_server.aws.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class AwsResponseDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "AWS S3 Presigned api 응답 DTO")
    public static class GetS3PresignedPatchUrl{
        @Schema(description = "S3에 업로드에 사용할 Presigned URL")
        private String presignedPatchUrl;

        @Schema(description = "실제 저장할 가능한 파일 URL (KEY)")
        private String savedFileUrl;

        @Schema(description = "임시 접근 가능한 Presigned URL")
        private String presignedGetUrl;

        public static GetS3PresignedPatchUrl of(String presignedPatchUrl, String savedFileUrl, String presignedGetUrl){
            return GetS3PresignedPatchUrl.builder().presignedPatchUrl(presignedPatchUrl).savedFileUrl(savedFileUrl).presignedGetUrl(presignedGetUrl).build();
        }
    }
}
