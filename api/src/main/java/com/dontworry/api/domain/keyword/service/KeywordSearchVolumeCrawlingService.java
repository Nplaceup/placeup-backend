package com.dontworry.api.domain.keyword.service;

import com.dontworry.api.controller.callback.dto.KeywordCallbackRequest;
import com.dontworry.api.domain.keyword.repository.KeywordSearchVolumeCrawlingRepository;
import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumeCrawling;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KeywordSearchVolumeCrawlingService {

    private final KeywordSearchVolumeCrawlingRepository keywordSearchVolumeCrawlingRepository;

    public LocalDate decideCrawlDate(Keywords keyword) {
        boolean hasTodayCompleted = keywordSearchVolumeCrawlingRepository
                .existsByKeyword_IdAndCrawlDateAndStatus(keyword.getId(), LocalDate.now(), CrawlingStatus.COMPLETED);

        return keyword.decideCrawlDate(hasTodayCompleted);
    }

    @Transactional
    public void updateCrawlingStatus(Keywords keyword,
                                      KeywordCallbackRequest request,
                                      CrawlingStatus status) {
        keywordSearchVolumeCrawlingRepository
                .findByKeywordAndCrawlDate(keyword, request.crawlDate())
                .ifPresentOrElse(
                        // 있으면 → 상태 업데이트
                        crawling -> {
                            crawling.setStatus(status);
                            crawling.setFinishedAt(LocalDateTime.now());
                        },
                        // 없으면 → 새로 insert
                        () -> {
                            KeywordSearchVolumeCrawling newCrawling = KeywordSearchVolumeCrawling.builder()
                                    .keyword(keyword)
                                    .crawlDate(request.crawlDate())
                                    .status(status)
                                    .startedAt(LocalDateTime.now())
                                    .finishedAt(LocalDateTime.now())
                                    .build();
                            keywordSearchVolumeCrawlingRepository.save(newCrawling);
                        }
                );
    }
}
