package com.dontworry.api.infra.crawler.dto;

import lombok.Builder;

@Builder
public record RankSearchResponse(
        String keyword,
        Long naverPlaceId,

        // 순위 정보
        Integer rankNo,
        Integer totalPlaceCount,
        Integer visitorReviewCount,
        Integer blogReviewCount,
        Double totalScore,

        // 검색량 정보
        Integer monthlySearchVolume,
        String competitionLevel
) {}