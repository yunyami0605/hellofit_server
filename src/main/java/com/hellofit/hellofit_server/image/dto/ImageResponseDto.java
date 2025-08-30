package com.hellofit.hellofit_server.image.dto;

import com.hellofit.hellofit_server.image.ImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.awt.*;


/**
 * 이미지 응답 dto
 */
public class ImageResponseDto {

    /**
     * 변경에 필요한 이미지 데이터
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class DataBeforeMutation {
        private String objectKey; // DB에 저장된 S3 object key
        private String presignedUrl; // presigned URL

        public static DataBeforeMutation fromEntity(String objectKey, String presignedUrl) {
            return DataBeforeMutation.builder()
                .objectKey(objectKey)
                .presignedUrl(presignedUrl)
                .build();
        }
    }

    /**
     *
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Presigned {
        private String presignedUrl;

        public static Presigned from(String url) {
            return Presigned.builder()
                .presignedUrl(url)
                .build();
        }
    }
}
