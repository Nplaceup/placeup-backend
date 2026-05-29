package com.dontworry.scheduler.domain.place.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PlaceCrawlerMessage(
        Long placeId,
        LocalDate crawlDate,
        String callbackUrl
) {
}
