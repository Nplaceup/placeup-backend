package com.dontworry.scheduler.domain.ranking.service;

import com.dontworry.core.domain.ranking.entity.RankingCrawling;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingOperation;
import com.dontworry.scheduler.domain.ranking.dto.RankingCrawlerMessage;
import com.dontworry.scheduler.domain.ranking.publisher.RankingCrawlingPublisher;
import com.dontworry.scheduler.domain.ranking.repository.RankingCrawlingRepository;
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
public class RankingCrawlingService {

    private final RankingCrawlingRepository keywordsCrawlingRepository;
    private final RankingCrawlingPublisher publisher;

    @Value("${url.callback.rankings}")
    private String callbackUrl;

    @Transactional
    public void process() {
        List<RankingCrawling> crawlingList =
                keywordsCrawlingRepository.createAndGetTodayCrawlingList(LocalDate.now(), List.of(0L));
        log.info("crawlingList size: {}", crawlingList.size());

        crawlingList.forEach(history -> {
            Keywords keyword = history.getKeyword();
            CrawlingOperation operation = keyword.getIndustry() != null
                    && keyword.getIndustry().getOperationName() != null
                    ? keyword.getIndustry().getOperationName()
                    : CrawlingOperation.getPlacesList;

            RankingCrawlerMessage request = new RankingCrawlerMessage(
                    keyword.getKeywordName(),
                    operation,
                    LocalDate.now(),
                    callbackUrl
            );

            publisher.publishRankingCrawling(request);
        });
    }
}
