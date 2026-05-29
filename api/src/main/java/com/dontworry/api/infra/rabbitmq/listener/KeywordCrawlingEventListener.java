package com.dontworry.api.infra.rabbitmq.listener;

import com.dontworry.api.domain.keyword.service.KeywordSearchVolumeCrawlingService;
import com.dontworry.api.infra.rabbitmq.dto.KeywordCrawlingEvent;
import com.dontworry.api.infra.rabbitmq.dto.KeywordCrawlerMessage;
import com.dontworry.api.infra.rabbitmq.publisher.KeywordCrawlerPublisher;
import com.dontworry.core.domain.keyword.entity.Keywords;
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
public class KeywordCrawlingEventListener {

    private final KeywordSearchVolumeCrawlingService keywordCrawlingService;
    private final KeywordCrawlerPublisher publisher;

    @Value("${url.callback.keywords}")
    private String callbackUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(KeywordCrawlingEvent event) {
        Keywords keyword = event.keyword();
        LocalDate crawlDate = keywordCrawlingService.decideCrawlDate(keyword);

        if (crawlDate != null) {
            KeywordCrawlerMessage message =
                    KeywordCrawlerMessage.builder()
                            .keyword(keyword.getKeywordName())
                            .crawlDate(crawlDate)
                            .callbackUrl(callbackUrl)
                            .build();

            publisher.publish(message);
            log.info("[CRAWLING-TRIGGER] success keyword={}, crawlDate={}", keyword, crawlDate);
        } else {
            log.info("[CRAWLING-TRIGGER] passed, already crawled for today, keyword={}", keyword);
        }
    }
}
