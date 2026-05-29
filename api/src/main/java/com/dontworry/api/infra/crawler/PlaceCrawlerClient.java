package com.dontworry.api.infra.crawler;

import com.dontworry.api.infra.crawler.dto.PlaceDetailCrawlerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceCrawlerClient {

    private final RestTemplate restTemplate;

    @Value("${url.crawler.place}")
    private String placeCrawlerUrl;

    public PlaceDetailCrawlerResponse fetchDetail(Long placeId) {
        try {
            log.info("[PlaceCrawlerClient] 요청 placeId={}", placeId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Long> body = Map.of("placeId", placeId);
            HttpEntity<Map<String, Long>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<PlaceDetailCrawlerResponse> response = restTemplate
                    .postForEntity(placeCrawlerUrl, entity, PlaceDetailCrawlerResponse.class);

            log.info("[PlaceCrawlerClient] 완료 placeId={}", placeId);
            return response.getBody();
        } catch (Exception e) {
            log.error("[PlaceCrawlerClient] 실패 placeId={}, error={}", placeId, e.getMessage());
            return null;
        }
    }
}