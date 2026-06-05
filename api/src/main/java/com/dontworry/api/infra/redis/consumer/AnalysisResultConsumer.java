package com.dontworry.api.infra.redis.consumer;

import com.dontworry.api.domain.analysis.repository.CompetitorAnalysisResultRepository;
import com.dontworry.api.domain.place.service.AnalysisStatusService;
import com.dontworry.api.usecase.ranking.RankSearchUseCase;
import com.dontworry.core.modeling.entity.CompetitorAnalysisResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dontworry.api.infra.redis.publisher.AnalysisRedisPublisher;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.place.enums.AnalysisStatusType;
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
    private final AnalysisStatusService analysisStatusService;
    private final CompetitorAnalysisResultRepository competitorAnalysisResultRepository;

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
        analysisStatusService.update(place.getId(), AnalysisStatusType.RANKING_CRAWLING);

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
        analysisStatusService.update(place.getId(), AnalysisStatusType.SEO_ANALYZING);
        analysisRedisPublisher.requestAnalysisRound2(place.getId());
    }

    // 2차 결과 처리:
    // Python이 recommend_keywords, seo_results 테이블에 직접 저장 완료
    // Spring은 수신 확인 로그만 출력
    private void processRound2(Places place, JsonNode node) {
        log.info("[Redis] 2차 분석 완료 placeId={}", place.getId());

        JsonNode seoNode        = node.get("seo");
        JsonNode feedbackNode   = node.get("feedback");
        JsonNode competitorNode = node.get("competitor"); // ← 신규

        if (seoNode == null || feedbackNode == null) {
            log.warn("[Redis] SEO 데이터 없음 placeId={}", place.getId());
            analysisStatusService.update(place.getId(), AnalysisStatusType.FAILED);
            return;
        }

        // ── 경쟁업체 분석 결과 저장 (competitor 블록이 있을 때만) ──────────────
        if (competitorNode != null) {
            try {
                CompetitorAnalysisResult car = CompetitorAnalysisResult.builder()
                        .placeId(place.getId())
                        .competitorCount(competitorNode.get("count").asInt(0))
                        .competitorNames(competitorNode.get("names").toString())
                        .gapKeywords(competitorNode.get("gapKeywords").toString())
                        .rankGapKeywords(competitorNode.get("rankGapKeywords").toString())
                        .advantageKeywords(competitorNode.get("advantageKeywords").toString())
                        .categoryGap(competitorNode.get("categoryGap").toString())
                        .build();

                // upsert: 기존 레코드 있으면 덮어쓰기
                competitorAnalysisResultRepository.findByPlaceId(place.getId())
                        .ifPresentOrElse(
                                existing -> {
                                    // dirty-check 방식 또는 delete+save
                                    competitorAnalysisResultRepository.delete(existing);
                                    competitorAnalysisResultRepository.save(car);
                                },
                                () -> competitorAnalysisResultRepository.save(car)
                        );

                log.info("[Redis] 경쟁업체 분석 저장 완료 placeId={}, count={}",
                        place.getId(), car.getCompetitorCount());
            } catch (Exception e) {
                log.warn("[Redis] 경쟁업체 분석 저장 실패 placeId={}, error={}", place.getId(), e.getMessage());
            }
        }

        log.info("[Redis] SEO 수신 완료 placeId={}, score={}, grade={}",
                place.getId(),
                seoNode.get("total").asInt(),
                seoNode.get("grade").asText());

        analysisStatusService.update(place.getId(), AnalysisStatusType.COMPLETED);
    }
}