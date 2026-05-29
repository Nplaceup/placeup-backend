package com.dontworry.api.infra.rabbitmq.dto;

import com.dontworry.api.controller.callback.dto.RankingCallbackResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PlaceCrawlingEvent(
        LocalDate crawlDate,
        List<RankingCallbackResponse.RankingResponse> rankings
) {
}
