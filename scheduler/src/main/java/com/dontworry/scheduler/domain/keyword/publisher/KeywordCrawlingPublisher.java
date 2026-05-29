package com.dontworry.scheduler.domain.keyword.publisher;

import com.dontworry.scheduler.common.config.RabbitMqConfig;
import com.dontworry.scheduler.domain.keyword.dto.KeywordCrawlerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordCrawlingPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishKeywordCrawling(KeywordCrawlerMessage request) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.CRAWLER_EXCHANGE,
                RabbitMqConfig.CRAWLER_KEYWORD_ROUTING_KEY,
                request
        );
    }

}
