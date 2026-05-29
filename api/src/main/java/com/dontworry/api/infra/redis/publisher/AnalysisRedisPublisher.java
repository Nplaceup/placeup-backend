package com.dontworry.api.infra.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisRedisPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // 1차 분석 요청 (리뷰 기반)
    public void requestAnalysisRound1(Long placeId) {
        push(placeId, 1);
    }

    // 2차 분석 요청 (순위/검색량 포함)
    public void requestAnalysisRound2(Long placeId) {
        push(placeId, 2);
    }

    private void push(Long placeId, int round) {
        try {
            String payload = objectMapper.writeValueAsString(
                    Map.of("place_id", placeId, "round", round)
            );
            redisTemplate.opsForList().leftPush("analysis:queue", payload);
            log.info("[Redis] 분석 요청 적재 placeId={}, round={}", placeId, round);
        } catch (Exception e) {
            log.error("[Redis] 분석 요청 실패 placeId={}, round={}, error={}", placeId, round, e.getMessage());
        }
    }
}