package com.dontworry.api.domain.ranking.service;

import com.dontworry.api.domain.ranking.repository.RankingCrawlingRepository;
import com.dontworry.core.domain.ranking.entity.RankingCrawling;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankingCrawlingService {

    private final RankingCrawlingRepository rankingCrawlingRepository;

    @Transactional
    public void createOrUpdateRankingCrawlingWithKeywordAndCrawlDate(Keywords keyword, LocalDate crawlDate) {
        Optional<RankingCrawling> optionalCrawling = rankingCrawlingRepository.findByKeywordAndCrawlDate(keyword, crawlDate);

        RankingCrawling crawling;
        if(optionalCrawling.isEmpty()) {
            crawling = RankingCrawling.buildStarted(keyword, crawlDate);
        } else {
            crawling = optionalCrawling.get();
            crawling.setStatus(CrawlingStatus.PROCESSING);
        }

        rankingCrawlingRepository.save(crawling);
    }

    @Transactional
    public void updateRankingCrawlingStatus(Keywords keyword, LocalDate crawlDate, CrawlingStatus status) {
        Optional<RankingCrawling> optionalHistory = rankingCrawlingRepository.findByKeywordAndCrawlDate(keyword, crawlDate);

        RankingCrawling history;
        if(optionalHistory.isEmpty()) {
            history = RankingCrawling.build(keyword, crawlDate, status);
        } else {
            history = optionalHistory.get();
            history.finishWithStatus(status);
        }

        rankingCrawlingRepository.save(history);
    }

    public LocalDate decideCrawlDate(Keywords keyword) {
        boolean hasTodayCompleted = rankingCrawlingRepository
                .existsByKeyword_IdAndCrawlDateAndStatus(keyword.getId(), LocalDate.now(), CrawlingStatus.COMPLETED);

        return keyword.decideCrawlDate(hasTodayCompleted);
    }

}
