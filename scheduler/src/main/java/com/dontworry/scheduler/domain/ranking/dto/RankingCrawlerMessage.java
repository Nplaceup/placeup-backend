package com.dontworry.scheduler.domain.ranking.dto;

import com.dontworry.core.domain.keyword.enums.CrawlingOperation;

import java.time.LocalDate;

public record RankingCrawlerMessage(
        String keyword,
        CrawlingOperation operation,
        LocalDate crawlDate,

        String callbackUrl
) {
}
