package com.hellofit.hellofit_server.global.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 커서 방식 리스트 응답
 *
 * @param <T>
 */
@Getter
@Builder
public class CursorResponse<T> {
    private List<T> items;        // 실제 데이터
    private String nextCursor;    // 마지막 데이터의 createdAt
    private boolean hasNext;      // 다음 페이지 있는지 여부
}
