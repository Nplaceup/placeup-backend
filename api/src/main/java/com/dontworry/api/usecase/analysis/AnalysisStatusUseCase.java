package com.dontworry.api.usecase.analysis;

import com.dontworry.api.controller.analysis.dto.AnalysisStatusResponse;
import com.dontworry.api.domain.analysis.repository.RecommendKeywordRepository;
import com.dontworry.api.domain.analysis.repository.SeoResultRepository;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.api.domain.place.service.AnalysisStatusService;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.place.enums.AnalysisStatusType;
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
public class AnalysisStatusUseCase {

    private final PlacesRepository placesRepository;
    private final AnalysisStatusService analysisStatusService;
    private final RecommendKeywordRepository recommendKeywordRepository;
    private final SeoResultRepository seoResultRepository;

    @Transactional(readOnly = true)
    public AnalysisStatusResponse getStatus(Long naverPlaceId) {

        Places place = placesRepository.findByNaverPlaceId(naverPlaceId);
        if (place == null) {
            log.info("[AnalysisStatus] 플레이스 없음 naverPlaceId={}", naverPlaceId);
            return AnalysisStatusResponse.noHistory(naverPlaceId);
        }

        AnalysisStatusType status = analysisStatusService.getStatus(place.getId());

        List<RecommendKeyword> keywords =
                recommendKeywordRepository.findByPlaceIdOrderByScoreDesc(
                        place.getId().intValue()
                );

        SeoResult seoResult = seoResultRepository
                .findByPlaceId(place.getId())
                .orElse(null);

        // analysis_status 기록이 없는 케이스 처리
        // (analysis_status 테이블 도입 이전에 분석된 데이터, 또는 PlaceAnalysisUseCase에서 재요청 없이 대기한 경우)
        if (status == null) {
            // keyword + SEO 모두 있으면 → 완료
            if (!keywords.isEmpty() && seoResult != null) {
                log.info("[AnalysisStatus] 상태 기록 없음, 분석 결과 존재 → COMPLETED 응답 naverPlaceId={}, placeId={}, keywords={}",
                        naverPlaceId, place.getId(), keywords.size());
                return AnalysisStatusResponse.completed(place, keywords, seoResult);
            }

            // keyword만 있으면 → SEO 분석 진행 중
            if (!keywords.isEmpty()) {
                log.info("[AnalysisStatus] 상태 기록 없음, 키워드 존재 → SEO 분석 진행 중 naverPlaceId={}, placeId={}",
                        naverPlaceId, place.getId());
                return AnalysisStatusResponse.analyzing(place, AnalysisStatusType.SEO_ANALYZING);
            }

            // 아무것도 없으면 → 분석 이력 없음
            log.info("[AnalysisStatus] 분석 이력 없음 naverPlaceId={}, placeId={}", naverPlaceId, place.getId());
            return AnalysisStatusResponse.noHistory(naverPlaceId);
        }

        // COMPLETED 상태인데 SEO 결과가 없으면 → DB 불일치, SEO 분석 진행 중으로 처리
        if (status == AnalysisStatusType.COMPLETED && seoResult == null) {
            log.info("[AnalysisStatus] COMPLETED이나 SEO 결과 없음 → SEO 분석 진행 중 naverPlaceId={}, placeId={}",
                    naverPlaceId, place.getId());
            return AnalysisStatusResponse.analyzing(place, AnalysisStatusType.SEO_ANALYZING);
        }

        // 정상 완료
        if (status == AnalysisStatusType.COMPLETED) {
            log.info("[AnalysisStatus] 완료 naverPlaceId={}, placeId={}, keywords={}",
                    naverPlaceId, place.getId(), keywords.size());
            return AnalysisStatusResponse.completed(place, keywords, seoResult);
        }

        // 실패
        if (status == AnalysisStatusType.FAILED) {
            log.warn("[AnalysisStatus] 실패 naverPlaceId={}, placeId={}", naverPlaceId, place.getId());
            return AnalysisStatusResponse.analyzing(place, status);
        }

        // 진행 중 (REQUESTED, PLACE_CRAWLING, REVIEW_CRAWLING, KEYWORD_EXTRACTING,
        //          RANKING_CRAWLING, SEARCH_VOLUME_CRAWLING, SEO_ANALYZING)
        log.info("[AnalysisStatus] 진행 중 naverPlaceId={}, placeId={}, status={}", naverPlaceId, place.getId(), status);
        return AnalysisStatusResponse.analyzing(place, status);
    }
}