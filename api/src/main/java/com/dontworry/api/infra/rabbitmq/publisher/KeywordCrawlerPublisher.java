package com.dontworry.api.infra.rabbitmq.publisher;

import com.dontworry.api.common.config.RabbitMqConfig;
import com.dontworry.api.infra.rabbitmq.dto.KeywordCrawlerMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeywordCrawlerPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(KeywordCrawlerMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.CRAWLER_EXCHANGE,
                RabbitMqConfig.KEYWORD_CRAWLER_ROUTING_KEY,
                message
        );
    }
}
