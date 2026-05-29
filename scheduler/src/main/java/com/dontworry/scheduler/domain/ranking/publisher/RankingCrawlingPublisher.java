package com.dontworry.scheduler.domain.ranking.publisher;

import com.dontworry.scheduler.common.config.RabbitMqConfig;
import com.dontworry.scheduler.domain.ranking.dto.RankingCrawlerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingCrawlingPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishRankingCrawling(RankingCrawlerMessage request) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.CRAWLER_EXCHANGE,
                RabbitMqConfig.CRAWLER_RANKING_ROUTING_KEY,
                request
        );
    }
    
}
