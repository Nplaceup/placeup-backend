package com.dontworry.api.infra.rabbitmq.listener;

import com.dontworry.api.infra.rabbitmq.dto.RankingCrawlingEvent;
import com.dontworry.api.domain.ranking.service.RankingCrawlingService;
import com.dontworry.api.infra.rabbitmq.dto.RankingCrawlerMessage;
import com.dontworry.api.infra.rabbitmq.publisher.RankingCrawlerPublisher;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingCrawlingEventListener {

    private final RankingCrawlingService rankingCrawlingService;

    private final RankingCrawlerPublisher publisher;

    @Value("${url.callback.rankings}")
    private String callbackUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(RankingCrawlingEvent event) {
        Keywords keyword = event.keyword();

        LocalDate crawlDate = rankingCrawlingService.decideCrawlDate(keyword);

        if(crawlDate != null){
            CrawlingOperation operation = keyword.getIndustry() != null
                    && keyword.getIndustry().getOperationName() != null
                    ? keyword.getIndustry().getOperationName()
                    : CrawlingOperation.getPlacesList;

            RankingCrawlerMessage message =
                    RankingCrawlerMessage.builder()
                            .keyword(keyword.getKeywordName())
                            .operation(operation)
                            .crawlDate(crawlDate)
                            .callbackUrl(callbackUrl)
                            .build();

            publishMessage(message);

            rankingCrawlingService.createOrUpdateRankingCrawlingWithKeywordAndCrawlDate(keyword, crawlDate);
            log.info("[CRAWLING-TRIGGER] success keyword={}, operation={}, crawlDate={}", keyword, operation, crawlDate);
        } else {
            log.info("[CRAWLING-TRIGGER] passed, already crawled for today, keyword={}", keyword);
        }
    }

    private void publishMessage(RankingCrawlerMessage message) {
        try {
            publisher.publish(message);
            log.info("순위 크롤링 MQ 발행 성공");
        } catch (Exception e) {
            log.error("순위 크롤링 MQ 발행 실패", e);
        }
    }
}
