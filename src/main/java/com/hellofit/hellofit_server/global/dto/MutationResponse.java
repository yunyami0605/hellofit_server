package com.hellofit.hellofit_server.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class MutationResponse {
    private Boolean success;
    private UUID id;

    public static MutationResponse of(Boolean success) {
        return MutationResponse.builder()
                               .success(success)
                               .build();
    }

    public static MutationResponse create(Boolean success, UUID id) {
        return MutationResponse.builder()
                               .success(success)
                               .id(id)
                               .build();
    }
}
