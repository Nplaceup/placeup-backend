package com.dontworry.api.usecase.analysis;

import com.dontworry.api.controller.analysis.dto.PlaceAnalysisResponse;
import com.dontworry.api.infra.crawler.dto.PlaceInfoResponse;
import com.dontworry.api.infra.redis.publisher.AnalysisRedisPublisher;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.api.domain.place.service.PlaceService;
import com.dontworry.api.infra.crawler.PlaceHtmlClient;
import com.dontworry.core.domain.place.entity.Places;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceAnalysisUseCase {

    private final PlaceHtmlClient placeHtmlClient;
    private final PlaceService placeService;
    private final PlacesRepository placesRepository;
    private final AnalysisRedisPublisher analysisRedisPublisher;

    @Transactional
    public PlaceAnalysisResponse getAnalysis(String url) {

        // 1. place 기본정보 조회 (url -> placeId, placeName)
        PlaceInfoResponse placeInfo = placeHtmlClient.validateUserPlacesUrl(url);
        Long naverPlaceId = placeInfo.getNaverPlaceId();

        Places place = placesRepository.findByNaverPlaceId(naverPlaceId);
        if (place == null) {
            place = placeService.createOrUpdatePlaces(placeInfo);
        }

        // 분석 요청 전, DB에 데이터가 있는지 조회
        // recommend_keywords, seo_results
        // 둘 다 없다 = 1
        // recommend_keywords만 있다 = 2
        // seo_results도 있다 = 3

        // 둘 다 있다면 상태 값 + 결과 데이터 응답
        log.info("[PlaceAnalysis] 분석 요청 naverPlaceId={}", naverPlaceId);
        analysisRedisPublisher.requestAnalysisRound1(place.getId());

        return PlaceAnalysisResponse.analyzing(place);
    }
}