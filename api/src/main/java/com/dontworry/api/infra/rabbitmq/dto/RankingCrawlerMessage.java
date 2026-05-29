package com.dontworry.api.infra.rabbitmq.dto;

import com.dontworry.core.domain.keyword.enums.CrawlingOperation;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RankingCrawlerMessage(
        String keyword,
        CrawlingOperation operation,
        LocalDate crawlDate,

        String callbackUrl
) {}
