package com.dontworry.api.usecase.analysis;

import com.dontworry.api.controller.analysis.dto.PlaceAnalysisResponse;
import com.dontworry.api.domain.analysis.repository.RecommendKeywordRepository;
import com.dontworry.api.domain.analysis.repository.SeoResultRepository;
import com.dontworry.api.domain.place.repository.PlaceReviewRepository;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.api.domain.place.service.PlaceService;
import com.dontworry.api.infra.crawler.PlaceHtmlClient;
import com.dontworry.api.infra.crawler.dto.PlaceInfoResponse;
import com.dontworry.api.infra.redis.publisher.AnalysisRedisPublisher;
import com.dontworry.api.usecase.place.PlaceDetailUseCase;
import com.dontworry.core.domain.place.entity.Places;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceAnalysisUseCase {

    private final PlaceHtmlClient placeHtmlClient;
    private final PlaceService placeService;
    private final PlacesRepository placesRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final RecommendKeywordRepository recommendKeywordRepository;
    private final SeoResultRepository seoResultRepository;
    private final AnalysisRedisPublisher analysisRedisPublisher;
    private final PlaceDetailUseCase placeDetailUseCase;


    public PlaceAnalysisResponse getAnalysis(String url) {

        PlaceInfoResponse placeInfo = placeHtmlClient.validateUserPlacesUrl(url);
        Long naverPlaceId = placeInfo.getNaverPlaceId();

        Places place = placesRepository.findByNaverPlaceId(naverPlaceId);
        if (place == null) {
            place = placeService.createOrUpdatePlaces(placeInfo);
        }

        boolean hasKeywords = !recommendKeywordRepository
                .findByPlaceIdOrderByScoreDesc(place.getId().intValue()).isEmpty();
        boolean hasSeo = seoResultRepository.findByPlaceId(place.getId()).isPresent();

        // 키워드 + SEO 둘 다 완료 → 재분석 없이 결과 반환
        if (hasKeywords && hasSeo) {
            log.info("[PlaceAnalysis] 분석 완료 상태 naverPlaceId={}", naverPlaceId);
            return PlaceAnalysisResponse.of(place);
        }

        // 키워드 또는 SEO 중 하나라도 결과 존재 → 분석 진행 중이므로 재요청 없이 대기 응답
        if (hasKeywords || hasSeo) {
            log.info("[PlaceAnalysis] 분석 진행 중 상태 naverPlaceId={}", naverPlaceId);
            return PlaceAnalysisResponse.analyzing(place);
        }

        // 둘 다 없을 때만 분석 요청
        log.info("[PlaceAnalysis] 분석 미시작 → 분석 요청 naverPlaceId={}", naverPlaceId);
        requestAnalysisWithReviewCheck(url, place, naverPlaceId);
        return PlaceAnalysisResponse.analyzing(place);
    }

    private void requestAnalysisWithReviewCheck(String url, Places place, Long naverPlaceId) {
        boolean hasReviews = placeReviewRepository.existsByPlace(place);
        if (hasReviews) {
            log.info("[PlaceAnalysis] 리뷰 있음 → 분석 요청 naverPlaceId={}", naverPlaceId);
            analysisRedisPublisher.requestAnalysisRound1(place.getId());
        } else {
            log.info("[PlaceAnalysis] 리뷰 없음 → 크롤링 후 분석 요청 naverPlaceId={}", naverPlaceId);
            placeDetailUseCase.getPlaceDetail(url);
            analysisRedisPublisher.requestAnalysisRound1(place.getId());
        }
    }
}