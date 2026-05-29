package com.dontworry.api.infra.crawler.dto;

import java.time.LocalDate;
import java.util.List;

public record PlaceDetailCrawlerResponse(
        Long placeId,
        LocalDate crawlDate,
        Integer blogCafeReviewCount,
        Integer imageReviewCount,
        Integer newsPostCount,
        List<VisitorReview> visitorReviews,
        List<AnalysisItem> themes,
        List<AnalysisItem> menus,
        String description,
        String menuList
) {
    public record VisitorReview(
            String naverReviewId,
            String body,
            String visited
    ) {}
    public record AnalysisItem(String label, Integer count) {}
}