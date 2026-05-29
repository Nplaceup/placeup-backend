package com.dontworry.api.controller.analysis.dto;


import lombok.Builder;

import java.util.List;

@Builder
public record PlaceDetailResponse(
        Long naverPlaceId,
        String placeName,
        String category,
        String address,
        String phoneNumber,
        String placeUrl,
        Integer saveCount,
        Boolean reservationAvailable,
        Boolean naverPayAvailable,
        String talktalkUrl,
        Integer couponCount,
        Integer blogCafeReviewCount,

        List<ReviewItem> reviews,
        List<AnalysisItem> themes,
        List<AnalysisItem> menus
) {
    public record ReviewItem(
            String body,
            String visited
    ) {}

    public record AnalysisItem(
            String label,
            Integer count
    ) {}
}