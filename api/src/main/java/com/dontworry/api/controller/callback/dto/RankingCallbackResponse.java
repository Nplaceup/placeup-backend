package com.dontworry.api.controller.callback.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record RankingCallbackResponse(
        String keyword,
        LocalDate crawlDate,
        List<RankingResponse> crawlerResponses
) {
    public record RankingResponse(
            Long placeId, // place
            String placeName, // place
            String category, // place
            Integer rankNo, // rank
            String roadAddress, // place
            String url, // place
            Long total, // keyword
            Integer saveCount, // place
            String phoneNumber, // place
            Boolean reservationAvailable, // place
            Boolean naverPayAvailable, // place
            String talktalkUrl, // place
            Integer couponCount, // place
            Integer visitorReviewCount, // rank
            Integer blogReviewCount, // rank
            Double totalScore, // rank
            String cidList // place
    ) {}

}

