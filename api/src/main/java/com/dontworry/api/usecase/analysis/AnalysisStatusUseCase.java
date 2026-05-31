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
            return AnalysisStatusResponse.noHistory(naverPlaceId);
        }

        AnalysisStatusType status = analysisStatusService.getStatus(place.getId());
        if (status == null) {
            return AnalysisStatusResponse.noHistory(naverPlaceId);
        }

        if (status == AnalysisStatusType.COMPLETED) {
            List<RecommendKeyword> keywords =
                    recommendKeywordRepository.findByPlaceIdOrderByScoreDesc(
                            place.getId().intValue());
            SeoResult seoResult = seoResultRepository
                    .findByPlaceId(place.getId())
                    .orElse(null);

            log.info("[AnalysisStatus] 완료 naverPlaceId={}, keywords={}", naverPlaceId, keywords.size());
            return AnalysisStatusResponse.completed(place, keywords, seoResult);
        }

        if (status == AnalysisStatusType.FAILED) {
            log.warn("[AnalysisStatus] 실패 naverPlaceId={}", naverPlaceId);
            return AnalysisStatusResponse.analyzing(place, status);
        }

        log.info("[AnalysisStatus] 진행 중 naverPlaceId={}, status={}", naverPlaceId, status);
        return AnalysisStatusResponse.analyzing(place, status);
    }
}
