package com.dontworry.api.controller.callback.dto;

import java.time.LocalDate;
import java.util.List;

// controller/callback/dto/PlaceCallbackResponse.java
public record PlaceCallbackResponse(
        Long placeId,
        LocalDate crawlDate,
        Integer blogCafeReviewCount,
        Integer imageReviewCount,
        Integer newsPostCount,
        List<VisitorReviewItem> visitorReviews,
        List<ReviewAnalysisItem> themes,
        List<ReviewAnalysisItem> menus,
        String description,
        String menuList
) {}
