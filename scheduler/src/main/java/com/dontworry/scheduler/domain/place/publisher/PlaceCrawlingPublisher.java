package com.dontworry.scheduler.domain.place.publisher;

import com.dontworry.scheduler.common.config.RabbitMqConfig;
import com.dontworry.scheduler.domain.place.dto.PlaceCrawlerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceCrawlingPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPlaceCrawling(PlaceCrawlerMessage request) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.CRAWLER_EXCHANGE,
                RabbitMqConfig.CRAWLER_PLACE_ROUTING_KEY,
                request
        );
    }
    
}
