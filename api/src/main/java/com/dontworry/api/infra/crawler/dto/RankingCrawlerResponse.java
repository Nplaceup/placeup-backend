package com.dontworry.api.infra.crawler.dto;

import java.time.LocalDate;
import java.util.List;

public record RankingCrawlerResponse(
        String keyword,
        LocalDate crawlDate,
        List<RankingItem> crawlerResponses
) {
    public record RankingItem(
            Long placeId,
            String placeName,
            String category,
            Integer rankNo,
            String roadAddress,
            Long total,
            Integer visitorReviewCount,
            Integer blogReviewCount,
            Double totalScore
    ) {}
}