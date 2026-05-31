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
import org.springframework.transaction.annotation.Transactional;

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

        // 1. place 기본정보 조회 (url -> placeId, placeName)
        PlaceInfoResponse placeInfo = placeHtmlClient.validateUserPlacesUrl(url);
        Long naverPlaceId = placeInfo.getNaverPlaceId();

        Places place = placesRepository.findByNaverPlaceId(naverPlaceId);
        if (place == null) {
            place = placeService.createOrUpdatePlaces(placeInfo);
        }

        // 2. 분석 결과 존재 여부 확인
        // 상태 1: recommend_keywords 없음 → 분석 요청
        // 상태 2: recommend_keywords만 있음 → 분석 중 (round2 대기)
        // 상태 3: seo_results까지 있음 → 분석 완료
        boolean hasKeywords = !recommendKeywordRepository
                .findByPlaceIdOrderByScoreDesc(place.getId().intValue())
                .isEmpty();
        boolean hasSeo = seoResultRepository.findByPlaceId(place.getId()).isPresent();

        if (hasSeo) {
            // 상태 3: 이미 분석 완료 → 재분석 요청
            log.info("[PlaceAnalysis] 상태3 재분석 요청 naverPlaceId={}", naverPlaceId);
            requestAnalysisWithReviewCheck(url, place, naverPlaceId);
            return PlaceAnalysisResponse.analyzing(place);
        }

        if (hasKeywords) {
            // 상태 2: round1 완료, round2 대기 중 → 중복 요청 방지
            log.info("[PlaceAnalysis] 상태2 분석 중 (round2 대기) naverPlaceId={}", naverPlaceId);
            return PlaceAnalysisResponse.analyzing(place);
        }

        // 상태 1: 분석 이력 없음 → 리뷰 확인 후 분석 요청
        log.info("[PlaceAnalysis] 상태1 최초 분석 요청 naverPlaceId={}", naverPlaceId);
        requestAnalysisWithReviewCheck(url, place, naverPlaceId);

        return PlaceAnalysisResponse.analyzing(place);
    }

    private void requestAnalysisWithReviewCheck(String url, Places place, Long naverPlaceId) {
        boolean hasReviews = placeReviewRepository.existsByPlace(place);
        if (hasReviews) {
            log.info("[PlaceAnalysis] 리뷰 있음 → 분석 요청 naverPlaceId={}", naverPlaceId);
            analysisRedisPublisher.requestAnalysisRound1(place.getId());
        } else {
            // 리뷰 없음 → PlaceDetailUseCase로 크롤링 + 저장 → 분석 요청
            log.info("[PlaceAnalysis] 리뷰 없음 → PlaceDetailUseCase 크롤링 naverPlaceId={}", naverPlaceId);
            placeDetailUseCase.getPlaceDetail(url);
            analysisRedisPublisher.requestAnalysisRound1(place.getId());
        }
    }
}