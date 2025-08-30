package com.hellofit.hellofit_server.post.dto;

import com.hellofit.hellofit_server.image.ImageEntity;
import com.hellofit.hellofit_server.post.PostEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.stream.IntStream;

public class PostRequestDto {

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class Create{
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(max = 80, message = "제목은 최대 80자까지 입력 가능합니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        // 클라이언트에서 새로 업로드된 이미지들의 objectKey 리스트
        private List<String> imageKeys;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class Update{
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(max = 80, message = "제목은 최대 80자까지 입력 가능합니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        private List<String> imageKeys;
    }
}
