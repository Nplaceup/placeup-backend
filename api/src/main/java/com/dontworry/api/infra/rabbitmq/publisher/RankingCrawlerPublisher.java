package com.dontworry.api.infra.rabbitmq.publisher;

import com.dontworry.api.common.config.RabbitMqConfig;
import com.dontworry.api.infra.rabbitmq.dto.RankingCrawlerMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingCrawlerPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(RankingCrawlerMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.CRAWLER_EXCHANGE,
                RabbitMqConfig.RANKING_CRAWLER_ROUTING_KEY,
                message
        );
    }
}
