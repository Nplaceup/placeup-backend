package com.dontworry.api.infra.rabbitmq.listener;

import com.dontworry.api.domain.place.service.PlaceCrawlingService;
import com.dontworry.api.domain.place.service.PlaceService;
import com.dontworry.api.infra.rabbitmq.dto.PlaceCrawlerMessage;
import com.dontworry.api.infra.rabbitmq.dto.PlaceCrawlingEvent;
import com.dontworry.api.infra.rabbitmq.publisher.PlaceCrawlingPublisher;
import com.dontworry.core.domain.place.entity.Places;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceCrawlingEventListener {

    private final PlaceCrawlingService placeCrawlingService;
    private final PlaceService placeService;

    private final PlaceCrawlingPublisher publisher;

    @Value("${url.callback.places}")
    private String callbackUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PlaceCrawlingEvent event) {
        event.rankings().forEach(ranking -> {
            PlaceCrawlerMessage message = PlaceCrawlerMessage.builder()
                    .placeId(ranking.placeId())
                    .crawlDate(event.crawlDate())
                    .cidList(ranking.cidList())
                    .callbackUrl(callbackUrl)
                    .build();

            publishMessage(message);

            Places place = placeService.getOrCreatePlaces(ranking.placeId(), ranking.url());
            placeCrawlingService.createOrUpdatePlaceCrawlingWithPlaceAndCrawlDate(
                    place, event.crawlDate());
        });
    }

    private void publishMessage(PlaceCrawlerMessage message) {
        try {
            publisher.publish(message);
            log.info("플레이스 크롤링 MQ 발행 성공");
        } catch (Exception e) {
            log.error("플레이스 크롤링 MQ 발행 실패", e);
        }
    }
}
