package com.dontworry.api.domain.place.service;

import com.dontworry.api.domain.place.repository.PlaceCrawlingRepository;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import com.dontworry.core.domain.place.entity.PlaceCrawling;
import com.dontworry.core.domain.place.entity.Places;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceCrawlingService {

    private final PlaceCrawlingRepository placeCrawlingRepository;

    @Transactional
    public void createOrUpdatePlaceCrawlingWithPlaceAndCrawlDate(Places place, LocalDate crawlDate){
        log.info("[PLACE-CRAWLING] naverPlaceId: {} found", place.getNaverPlaceId());
        Optional<PlaceCrawling> optionalCrawling = placeCrawlingRepository.findByPlaceAndCrawlDate(place, crawlDate);

        PlaceCrawling crawling;
        if(optionalCrawling.isEmpty()) {
            crawling = PlaceCrawling.buildStarted(place, crawlDate);
        } else {
            crawling = optionalCrawling.get();
            crawling.setCrawlingStatus(CrawlingStatus.PROCESSING);
        }

        placeCrawlingRepository.save(crawling);
        log.info("[PLACE-CRAWLING] place crawling created, {}", crawling.getId());
    }


    @Transactional
    public void updatePlaceCrawlingStatus(Places place, LocalDate crawlDate, CrawlingStatus status) {
        Optional<PlaceCrawling> optionalCrawling = placeCrawlingRepository.findByPlaceAndCrawlDate(place, crawlDate);
        log.info("[PLACE-CRAWLING] updated, placeId: {}, crawlDate: {}, status: {}", place.getId(), crawlDate, status);

        PlaceCrawling crawling;
        if(optionalCrawling.isEmpty()) {
            crawling = PlaceCrawling.build(place, crawlDate, status);
        } else {
            crawling = optionalCrawling.get();
            crawling.finishWithStatus(status);
        }

        placeCrawlingRepository.save(crawling);
        log.info("[PLACE-CRAWLING] updated, placeId: {}, crawlDate: {}, status: {}", place.getId(), crawlDate, status);
    }

}
