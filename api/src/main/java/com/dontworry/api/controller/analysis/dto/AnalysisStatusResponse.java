package com.dontworry.api.controller.analysis.dto;

import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.place.enums.AnalysisStatusType;
import com.dontworry.core.modeling.entity.RecommendKeyword;
import com.dontworry.core.modeling.entity.SeoResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnalysisStatusResponse {

    private Long               naverPlaceId;
    private String             placeName;
    private AnalysisStatusType status;
    private boolean            analyzing;

    private List<AnalysisResultResponse.KeywordItem> keywords;
    private AnalysisResultResponse.SeoItem           seo;
    private AnalysisResultResponse.FeedbackItem      feedback;

    public static AnalysisStatusResponse analyzing(Places place, AnalysisStatusType status) {
        return AnalysisStatusResponse.builder()
                .naverPlaceId(place.getNaverPlaceId())
                .placeName(place.getPlaceName())
                .status(status)
                .analyzing(true)
                .keywords(List.of())
                .seo(null)
                .feedback(null)
                .build();
    }

    public static AnalysisStatusResponse completed(
            Places place,
            List<RecommendKeyword> keywords,
            SeoResult seoResult) {

        AnalysisResultResponse result = AnalysisResultResponse.of(place, keywords, seoResult);

        return AnalysisStatusResponse.builder()
                .naverPlaceId(place.getNaverPlaceId())
                .placeName(place.getPlaceName())
                .status(AnalysisStatusType.COMPLETED)
                .analyzing(false)
                .keywords(result.getKeywords())
                .seo(result.getSeo())
                .feedback(result.getFeedback())
                .build();
    }

    public static AnalysisStatusResponse noHistory(Long naverPlaceId) {
        return AnalysisStatusResponse.builder()
                .naverPlaceId(naverPlaceId)
                .placeName("")
                .status(null)
                .analyzing(false)
                .keywords(List.of())
                .seo(null)
                .feedback(null)
                .build();
    }
}
