package com.dontworry.api.usecase.analysis;

import com.dontworry.api.controller.analysis.dto.AnalysisResultResponse;
import com.dontworry.api.domain.analysis.repository.RecommendKeywordRepository;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.api.domain.analysis.repository.SeoResultRepository;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.modeling.entity.RecommendKeyword;
import com.dontworry.core.modeling.entity.SeoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisResultUseCase {

    private final PlacesRepository placesRepository;
    private final RecommendKeywordRepository recommendKeywordRepository;
    private final SeoResultRepository seoResultRepository;

    @Transactional(readOnly = true)
    public AnalysisResultResponse getAnalysisResult(Long naverPlaceId) {

        // 1. 매장 조회
        Places place = placesRepository.findByNaverPlaceId(naverPlaceId);
        if (place == null) {
            log.warn("[AnalysisResult] 매장 없음 naverPlaceId={}", naverPlaceId);
            return AnalysisResultResponse.analyzing(
                    Places.builder()
                            .naverPlaceId(naverPlaceId)
                            .placeName("")
                            .build());
        }

        // 2. 추천 키워드 조회
        List<RecommendKeyword> keywords =
                recommendKeywordRepository.findByPlaceIdOrderByScoreDesc(
                        place.getId().intValue());

        // 키워드 없으면 분석 중
        if (keywords.isEmpty()) {
            log.info("[AnalysisResult] 분석 중 naverPlaceId={}", naverPlaceId);
            return AnalysisResultResponse.analyzing(place);
        }

        // 3. SEO 조회 (없으면 null — 개발 중)
        SeoResult seoResult = seoResultRepository
                .findByPlaceId(place.getId())
                .orElse(null);

        log.info("[AnalysisResult] 조회 완료 naverPlaceId={}, keywords={}, seo={}",
                naverPlaceId, keywords.size(), seoResult != null ? "있음" : "없음");

        return AnalysisResultResponse.of(place, keywords, seoResult);
    }
}