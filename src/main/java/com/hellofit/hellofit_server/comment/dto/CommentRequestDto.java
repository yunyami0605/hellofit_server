package com.hellofit.hellofit_server.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class CommentRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;
    }
}
