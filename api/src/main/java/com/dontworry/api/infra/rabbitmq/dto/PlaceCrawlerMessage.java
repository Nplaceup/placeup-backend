package com.dontworry.api.infra.rabbitmq.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PlaceCrawlerMessage(
        Long placeId,
        LocalDate crawlDate,
        String cidList,
        String callbackUrl
) {
}
