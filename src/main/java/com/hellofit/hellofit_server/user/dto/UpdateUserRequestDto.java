package com.hellofit.hellofit_server.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {

    @Schema(
        description = "사용자 닉네임",
        example = "test1"
    )
    private String nickname;

    // s3 업로드한 object key
    @Schema(
        description = "s3 profile image object key",
        example = "660e8400-e29b..."
    )
    private String profileImageKey;
}
