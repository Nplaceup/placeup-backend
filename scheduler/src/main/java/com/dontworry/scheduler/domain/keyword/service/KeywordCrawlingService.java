package com.dontworry.scheduler.domain.keyword.service;

import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumeCrawling;
import com.dontworry.scheduler.domain.keyword.dto.KeywordCrawlerMessage;
import com.dontworry.scheduler.domain.keyword.publisher.KeywordCrawlingPublisher;
import com.dontworry.scheduler.domain.keyword.repository.KeywordSearchVolumeCrawlingRepository;
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
public class KeywordCrawlingService {

    private final KeywordSearchVolumeCrawlingRepository keywordSearchVolumeCrawlingRepository;
    private final KeywordCrawlingPublisher publisher;

    @Value("${url.callback.keywords}")
    private String callbackUrl;

    @Transactional
    public void process() {
        List<KeywordSearchVolumeCrawling> crawlingList =
                keywordSearchVolumeCrawlingRepository.createAndGetThisMonthCrawlingList(LocalDate.now());
        log.info("KeywordCrawlingList size: {}", crawlingList.size());

        crawlingList.forEach(keywordCrawling -> {
            publisher.publishKeywordCrawling(KeywordCrawlerMessage.builder()
                    .keyword(keywordCrawling.getKeyword().getKeywordName())
                    .crawlDate(LocalDate.now())
                    .callbackUrl(callbackUrl)
                    .build());
        });
    }
}
