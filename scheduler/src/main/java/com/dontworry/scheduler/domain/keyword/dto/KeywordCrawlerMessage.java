package com.dontworry.scheduler.domain.keyword.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record KeywordCrawlerMessage(
        String keyword,
        LocalDate crawlDate,
        String callbackUrl
) {
}
