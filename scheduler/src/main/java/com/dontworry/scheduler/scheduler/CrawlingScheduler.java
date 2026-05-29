package com.dontworry.scheduler.scheduler;

import com.dontworry.scheduler.domain.keyword.service.KeywordCrawlingService;
import com.dontworry.scheduler.domain.ranking.service.RankingCrawlingService;
import com.dontworry.scheduler.domain.place.service.PlaceCrawlingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlingScheduler {

    private final RankingCrawlingService rankingCrawlingService;
    private final PlaceCrawlingService placeCrawlingService;
    private final KeywordCrawlingService keywordCrawlingService;

    @PostConstruct
    public void init() {
//        scheduleRankingCrawling();
//        schedulePlaceCrawling();
//        scheduleKeywordCrawling();
    }

    @Scheduled(cron = "0 0 14 * * *")
    public void scheduleRankingCrawling() {
        log.info("[RANKING-CRAWLER] scheduleCrawling Start");
        rankingCrawlingService.process();
        log.info("[RANKING-CRAWLER] scheduleCrawling End");
    }

    @Scheduled(cron = "0 0 2 1 * *")
    public void schedulePlaceCrawling() {
        log.info("[PLACE-CRAWLER] scheduleCrawling Start");
        placeCrawlingService.process();
        log.info("[PLACE-CRAWLER] scheduleCrawling End");
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void scheduleKeywordCrawling() {
        log.info("[KEYWORD-CRAWLER] scheduleCrawling Start");
        keywordCrawlingService.process();
        log.info("[KEYWORD-CRAWLER] scheduleCrawling End");
    }

}