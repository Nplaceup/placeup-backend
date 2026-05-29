package com.dontworry.api.infra.crawler;

import com.dontworry.core.domain.keyword.enums.CrawlingOperation;
import com.dontworry.api.infra.crawler.dto.RankingCrawlerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingCrawlerClient {

    private final RestTemplate restTemplate;

    @Value("${url.crawler.ranking}")
    private String rankingCrawlerUrl;

    public RankingCrawlerResponse fetchRanking(String keyword, CrawlingOperation operation) {
        try {
            log.info("[RankingCrawlerClient] 요청 keyword={}, operation={}", keyword, operation);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "keyword", keyword,
                    "operation", operation
            );
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<RankingCrawlerResponse> response = restTemplate
                    .postForEntity(rankingCrawlerUrl, entity, RankingCrawlerResponse.class);
            log.info("[RankingCrawlerClient] 완료 keyword={}", keyword);
            return response.getBody();
        } catch (Exception e) {
            log.error("[RankingCrawlerClient] 실패 keyword={}, error={}", keyword, e.getMessage());
            return null;
        }
    }
}