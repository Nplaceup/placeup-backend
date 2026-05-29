package com.dontworry.api.infra.crawler;

import com.dontworry.api.infra.crawler.dto.KeywordCrawlerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordCrawlerClient {

    private final RestTemplate restTemplate;

    @Value("${url.crawler.keyword}")
    private String keywordCrawlerUrl;

    public KeywordCrawlerResponse fetchKeyword(String keyword) {
        try {
            log.info("[KeywordCrawlerClient] 요청 keyword={}", keyword);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("keyword", keyword);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<KeywordCrawlerResponse> response = restTemplate
                    .postForEntity(keywordCrawlerUrl, entity, KeywordCrawlerResponse.class);
            log.info("[KeywordCrawlerClient] 완료 keyword={}", keyword);
            return response.getBody();
        } catch (Exception e) {
            log.error("[KeywordCrawlerClient] 실패 keyword={}, error={}", keyword, e.getMessage());
            return null;
        }
    }
}