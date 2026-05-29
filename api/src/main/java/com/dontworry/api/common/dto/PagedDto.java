package com.dontworry.api.common.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PagedDto<T> {

    private int totalPages;
    private int pageSize;
    private int currentPage;
    private Long totalCount;
    private List<T> contents;

    public static <T> PagedDto<T> toDto(int totalPages, int pageSize, int currentPage,
                                        Long totalCount, List<T> contents) {
        return PagedDto.<T>builder()
                .totalPages(totalPages)
                .pageSize(pageSize)
                .currentPage(currentPage)
                .totalCount(totalCount)
                .contents(contents)
                .build()
                ;
    }
}
