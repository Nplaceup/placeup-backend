package com.dontworry.scheduler.domain.place.service;

import com.dontworry.core.domain.place.entity.PlaceCrawling;
import com.dontworry.scheduler.domain.place.dto.PlaceCrawlerMessage;
import com.dontworry.scheduler.domain.place.publisher.PlaceCrawlingPublisher;
import com.dontworry.scheduler.domain.place.repository.PlaceCrawlingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceCrawlingService {

    private final PlaceCrawlingRepository placeCrawlingRepository;
    private final PlaceCrawlingPublisher publisher;

    @Value("${url.callback.places}")
    private String callbackUrl;

    @Transactional
    public void process() {
        List<PlaceCrawling> crawlingList = placeCrawlingRepository.createAndGetThisMonthCrawlingList(LocalDate.now());
        log.info("PlaceCrawlingList size: {}", crawlingList.size());

        crawlingList.forEach(placeCrawling -> {
            publisher.publishPlaceCrawling(PlaceCrawlerMessage.builder()
                    .placeId(placeCrawling.getPlace().getNaverPlaceId())
                    .crawlDate(LocalDate.now())
                    .callbackUrl(callbackUrl)
                    .build());
        });
    }
}
