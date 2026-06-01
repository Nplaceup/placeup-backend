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

        /*
         * 상태 3: 키워드 분석 결과와 SEO 분석 결과가 모두 존재
         * → 이미 분석 완료된 상태이므로 재분석 요청 없이 완료 응답 반환
         */
        if (hasKeywords && hasSeo) {
            log.info("[PlaceAnalysis] 분석 완료 상태 naverPlaceId={}", naverPlaceId);
            return PlaceAnalysisResponse.of(place);
        }

        /*
         * 상태 2: 키워드 분석 결과는 존재하지만 SEO 분석 결과는 없음
         * → Python round=2 로직도 현재 키워드 재계산을 포함하므로
         *   별도 Redis 요청을 보내지 않고 분석 진행 중으로 판단
         */
        if (hasKeywords) {
            log.info("[PlaceAnalysis] 키워드 분석 완료, SEO 분석 대기 중 naverPlaceId={}", naverPlaceId);
            return PlaceAnalysisResponse.analyzing(place);
        }

        /*
         * 상태 1: 키워드 분석 결과와 SEO 분석 결과가 모두 없음
         * → 최초 분석 요청
         */
        if (!hasSeo) {
            log.info("[PlaceAnalysis] 분석 결과 없음, 최초 분석 요청 naverPlaceId={}", naverPlaceId);
            requestAnalysisWithReviewCheck(url, place, naverPlaceId);
            return PlaceAnalysisResponse.analyzing(place);
        }

        /*
         * 예외 상태: SEO 결과는 있지만 키워드 결과가 없음
         * → 정상적인 분석 완료 상태가 아니므로 재분석 요청
         */
        log.warn("[PlaceAnalysis] 비정상 분석 상태: SEO 결과는 있으나 키워드 결과 없음 naverPlaceId={}", naverPlaceId);
        requestAnalysisWithReviewCheck(url, place, naverPlaceId);
        return PlaceAnalysisResponse.analyzing(place);
    }

    private void requestAnalysisWithReviewCheck(String url, Places place, Long naverPlaceId) {
        boolean hasReviews = placeReviewRepository.existsByPlace(place);
        if (hasReviews) {
            log.info("[PlaceAnalysis] 리뷰 있음 → round1 분석 요청 naverPlaceId={}", naverPlaceId);
            analysisRedisPublisher.requestAnalysisRound1(place.getId());
            return;
        }

        log.info("[PlaceAnalysis] 리뷰 없음 → 크롤링 후 round1 분석 요청 naverPlaceId={}", naverPlaceId);
        placeDetailUseCase.getPlaceDetail(url);
        analysisRedisPublisher.requestAnalysisRound1(place.getId());
    }
}