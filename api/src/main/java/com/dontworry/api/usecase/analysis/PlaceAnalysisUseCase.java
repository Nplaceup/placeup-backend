package com.dontworry.api.usecase.analysis;

import com.dontworry.api.controller.analysis.dto.PlaceAnalysisResponse;
import com.dontworry.api.domain.analysis.repository.RecommendKeywordRepository;
import com.dontworry.api.domain.analysis.repository.SeoResultRepository;
import com.dontworry.api.domain.place.repository.PlaceReviewRepository;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.api.domain.place.service.AnalysisStatusService;
import com.dontworry.api.domain.place.service.PlaceService;
import com.dontworry.api.infra.crawler.PlaceHtmlClient;
import com.dontworry.api.infra.crawler.dto.PlaceInfoResponse;
import com.dontworry.api.infra.redis.publisher.AnalysisRedisPublisher;
import com.dontworry.api.usecase.place.PlaceDetailUseCase;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.place.enums.AnalysisStatusType;
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
    private final AnalysisStatusService analysisStatusService;
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

        // 키워드 + SEO 둘 다 있으면 → 재분석 없이 완료 반환
        if (hasKeywords && hasSeo) {
            log.info("[PlaceAnalysis] 분석 완료 상태 naverPlaceId={}", naverPlaceId);
            return PlaceAnalysisResponse.of(place);
        }

        // 하나라도 없으면 → 분석 요청
        analysisStatusService.update(place.getId(), AnalysisStatusType.REQUESTED);
        requestAnalysisWithReviewCheck(url, place, naverPlaceId);
        return PlaceAnalysisResponse.analyzing(place);
    }

    private void requestAnalysisWithReviewCheck(String url, Places place, Long naverPlaceId) {
        boolean hasReviews = placeReviewRepository.existsByPlace(place);

        if (!hasReviews) {
            log.info("[PlaceAnalysis] 리뷰 없음 → 크롤링 후 분석 요청 naverPlaceId={}", naverPlaceId);
            analysisStatusService.update(place.getId(), AnalysisStatusType.PLACE_CRAWLING);
            placeDetailUseCase.getPlaceDetail(url);
        }

        log.info("[PlaceAnalysis] 리뷰 있음 → 분석 요청 naverPlaceId={}", naverPlaceId);
        analysisStatusService.update(place.getId(), AnalysisStatusType.KEYWORD_EXTRACTING);
        analysisRedisPublisher.requestAnalysisRound1(place.getId());
    }
}
