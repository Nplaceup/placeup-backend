package com.dontworry.api.infra.redis.consumer;

import com.dontworry.api.usecase.ranking.RankSearchUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dontworry.api.infra.redis.publisher.AnalysisRedisPublisher;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.core.domain.place.entity.Places;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisResultConsumer {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final PlacesRepository placesRepository;
    private final RankSearchUseCase rankSearchUseCase;
    private final AnalysisRedisPublisher analysisRedisPublisher;

    @Async
    public void startConsuming() {
        log.info("[Redis] 분석 결과 큐 대기 시작");
        while (true) {
            try {
                String json = redisTemplate.opsForList()
                        .rightPop("analysis:result:queue", Duration.ofSeconds(5));

                if (json == null) continue;

                processResult(json);

            } catch (Exception e) {
                log.error("[Redis] 결과 처리 오류: {}", e.getMessage());
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
        }
    }

    @Transactional
    public void processResult(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);

        Long placeId = node.get("place_id").asLong();
        int round    = node.get("round").asInt();

        Places place = placesRepository.findById(placeId).orElse(null);
        if (place == null) {
            log.warn("[Redis] place 없음 placeId={}", placeId);
            return;
        }

        if (round == 1) {
            processRound1(place, node);
        } else if (round == 2) {
            processRound2(place, node);
        }
    }

    // 1차 결과 처리: 키워드 목록 수신 → RankSearch → 2차 요청
    private void processRound1(Places place, JsonNode node) {
        log.info("[Redis] 1차 결과 수신 placeId={}", place.getId());

        // 키워드 목록 추출 (문자열 배열)
        List<String> keywords = new ArrayList<>();
        node.get("keywords").forEach(k -> keywords.add(k.asText()));

        log.info("[Redis] 1차 키워드 {}개 수신, RankSearch 시작", keywords.size());

        keywords.stream()
                .limit(10)
                .forEach(keyword -> {
                    try {
                        rankSearchUseCase.getRankSearch(keyword, place.getNaverPlaceId());
                        log.info("[RankSearch] 완료 keyword={}", keyword);
                    } catch (Exception e) {
                        log.warn("[RankSearch] 실패 keyword={}, error={}", keyword, e.getMessage());
                    }
                });

        log.info("[Redis] RankSearch 완료, 2차 분석 요청 placeId={}", place.getId());
        analysisRedisPublisher.requestAnalysisRound2(place.getId());
    }

    // 2차 결과 처리:
    // Python이 recommend_keywords, seo_results 테이블에 직접 저장 완료
    // Spring은 수신 확인 로그만 출력
    private void processRound2(Places place, JsonNode node) {
        log.info("[Redis] 2차 분석 완료 placeId={}", place.getId());

        JsonNode seoNode      = node.get("seo");
        JsonNode feedbackNode = node.get("feedback");

        if (seoNode == null || feedbackNode == null) {
            log.warn("[Redis] SEO 데이터 없음 placeId={}", place.getId());
            return;
        }

        // DB 저장은 Python이 완료 — Spring은 로그만 확인
        log.info("[Redis] SEO 수신 완료 placeId={}, score={}, grade={}",
                place.getId(),
                seoNode.get("total").asInt(),
                seoNode.get("grade").asText());

        log.info("[Redis] recommend_keywords, seo_results 저장 완료 (Python 직접 저장) placeId={}",
                place.getId());
    }
}