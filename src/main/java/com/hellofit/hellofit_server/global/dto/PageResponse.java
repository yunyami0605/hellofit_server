package com.hellofit.hellofit_server.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private boolean last;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private int number;

    public static <T> PageResponse<T> from(Page<T>page){
        return new PageResponse<>(
                page.getContent(),
                page.isLast(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.getNumber()
        );
    }
}
