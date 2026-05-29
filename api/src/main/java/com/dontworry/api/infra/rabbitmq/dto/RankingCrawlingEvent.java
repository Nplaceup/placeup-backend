package com.dontworry.api.infra.rabbitmq.dto;

import com.dontworry.core.domain.keyword.entity.Keywords;
import lombok.Builder;

@Builder
public record RankingCrawlingEvent(
    Keywords keyword
) {
}
