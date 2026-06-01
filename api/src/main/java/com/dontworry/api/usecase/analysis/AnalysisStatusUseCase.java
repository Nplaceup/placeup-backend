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

        /*
         * 기존 DB 데이터 보정 로직
         *
         * apply_changes.sh 적용 전에 이미 분석이 끝난 place는
         * recommend_keywords, seo_results에는 데이터가 있지만
         * analysis_status에는 데이터가 없을 수 있다.
         *
         * 따라서 status가 null이어도 결과 데이터가 있으면 COMPLETED로 간주한다.
         */
        if (status == null && !keywords.isEmpty() && seoResult != null) {
            log.info(
                    "[AnalysisStatus] 상태 기록 없음, 기존 분석 결과 존재 → COMPLETED 응답 naverPlaceId={}, placeId={}, keywords={}",
                    naverPlaceId,
                    place.getId(),
                    keywords.size()
            );

            return AnalysisStatusResponse.completed(place, keywords, seoResult);
        }

        /*
         * status는 없지만 keyword만 있는 경우 → SEO 분석 진행 중
         * Python round=2가 실행 중이거나, 서버 재시작 등으로 status 레코드가 유실된 상태
         */
        if (status == null && !keywords.isEmpty()) {
            log.info(
                    "[AnalysisStatus] 상태 기록 없음, 키워드 존재 → SEO 분석 진행 중으로 판단 naverPlaceId={}, placeId={}",
                    naverPlaceId,
                    place.getId()
            );

            return AnalysisStatusResponse.analyzing(place, AnalysisStatusType.SEO_ANALYZING);
        }

        /*
         * 상태도 없고 결과 데이터도 없으면
         * 아직 분석 요청 이력이 없는 것으로 처리한다.
         */
        if (status == null) {
            log.info(
                    "[AnalysisStatus] 분석 이력 없음 naverPlaceId={}, placeId={}",
                    naverPlaceId,
                    place.getId()
            );

            return AnalysisStatusResponse.noHistory(naverPlaceId);
        }

        /*
         * 분석 완료 상태
         * analysis_status가 COMPLETED이면 결과 데이터를 함께 반환한다.
         */
        if (status == AnalysisStatusType.COMPLETED) {
            log.info(
                    "[AnalysisStatus] 완료 naverPlaceId={}, placeId={}, keywords={}",
                    naverPlaceId,
                    place.getId(),
                    keywords.size()
            );

            return AnalysisStatusResponse.completed(place, keywords, seoResult);
        }

        /*
         * 분석 실패 상태
         */
        if (status == AnalysisStatusType.FAILED) {
            log.warn(
                    "[AnalysisStatus] 실패 naverPlaceId={}, placeId={}",
                    naverPlaceId,
                    place.getId()
            );

            return AnalysisStatusResponse.analyzing(place, status);
        }

        /*
         * 그 외 상태는 진행 중으로 반환
         * REQUESTED, PLACE_CRAWLING, REVIEW_CRAWLING,
         * KEYWORD_EXTRACTING, RANKING_CRAWLING,
         * SEARCH_VOLUME_CRAWLING, SEO_ANALYZING 등
         */
        log.info(
                "[AnalysisStatus] 진행 중 naverPlaceId={}, placeId={}, status={}",
                naverPlaceId,
                place.getId(),
                status
        );

        return AnalysisStatusResponse.analyzing(place, status);
    }
}