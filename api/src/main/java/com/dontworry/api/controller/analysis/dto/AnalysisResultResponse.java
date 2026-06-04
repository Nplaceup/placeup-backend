package com.dontworry.api.controller.analysis.dto;

import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.modeling.entity.RecommendKeyword;
import com.dontworry.core.modeling.entity.SeoResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class AnalysisResultResponse {

    private Long    naverPlaceId;
    private String  placeName;
    private boolean analyzing;

    private List<KeywordItem> keywords;
    private SeoItem           seo;
    private FeedbackItem      feedback;

    // ── 내부 클래스 ──────────────────────────────────────────────────────

    @Getter
    @Builder
    public static class KeywordItem {
        private String  keyword;
        private Double  score;
        private Integer monthlySearchVolume;
        private Integer rankNo;
        private String  competitionLevel;
        @JsonProperty("isOpportunity")
        private Boolean isOpportunity;
    }

    @Getter
    @Builder
    public static class SeoItem {
        private Integer score;
        private String  grade;
        // Python place_scorer breakdown 기준
        private Double  placeCompleteness;
        private Double  reviewQuality;
    }

    @Getter
    @Builder
    public static class FeedbackItem {
        private String       summary;
        private List<String> seoFeedback;
        private List<String> reviewFeedback;
        private List<String> competitorFeedback;
        // {category: [keyword, ...]} 형태의 카테고리별 키워드 요약
        private Map<String, List<String>> placeSummary;
    }

    // ── 팩토리 메서드 ─────────────────────────────────────────────────────

    // 분석 완료
    public static AnalysisResultResponse of(
            Places place,
            List<RecommendKeyword> keywords,
            SeoResult seoResult) {

        // SEO (null 가능 — 개발 중)
        SeoItem      seoItem      = null;
        FeedbackItem feedbackItem = null;

        if (seoResult != null) {
            seoItem = SeoItem.builder()
                    .score(seoResult.getScore())
                    .grade(seoResult.getGrade())
                    .placeCompleteness(seoResult.getPlaceCompleteness())
                    .reviewQuality(seoResult.getReviewQuality())
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            List<String> seoFeedback;
            List<String> reviewFeedback;
            List<String> competitorFeedback;
            Map<String, List<String>> placeSummary;
            try {
                seoFeedback         = mapper.readValue(seoResult.getSeoFeedback(),
                        new TypeReference<List<String>>() {});
                reviewFeedback      = mapper.readValue(seoResult.getReviewFeedback(),
                        new TypeReference<List<String>>() {});
                competitorFeedback  = mapper.readValue(seoResult.getCompetitorFeedback(),
                        new TypeReference<List<String>>() {});
                placeSummary        = mapper.readValue(seoResult.getPlaceSummary(),
                        new TypeReference<Map<String, List<String>>>() {});
            } catch (Exception e) {
                seoFeedback        = List.of();
                reviewFeedback     = List.of();
                competitorFeedback = List.of();
                placeSummary       = Map.of();
            }

            feedbackItem = FeedbackItem.builder()
                    .summary(seoResult.getSummary())
                    .seoFeedback(seoFeedback)
                    .reviewFeedback(reviewFeedback)
                    .competitorFeedback(competitorFeedback)
                    .placeSummary(placeSummary)
                    .build();
        }

        return AnalysisResultResponse.builder()
                .naverPlaceId(place.getNaverPlaceId())
                .placeName(place.getPlaceName())
                .analyzing(false)
                .keywords(keywords.stream()
                        .map(k -> KeywordItem.builder()
                                .keyword(k.getKeyword())
                                .score(k.getScore())
                                .monthlySearchVolume(k.getMonthlySearchVolume())
                                .rankNo(k.getRankNo())
                                .competitionLevel(k.getCompetitionLevel())
                                .isOpportunity(k.getIsOpportunity())
                                .build())
                        .toList())
                .seo(seoItem)
                .feedback(feedbackItem)
                .build();
    }

    // 분석 중
    public static AnalysisResultResponse analyzing(Places place) {
        return AnalysisResultResponse.builder()
                .naverPlaceId(place.getNaverPlaceId())
                .placeName(place.getPlaceName())
                .analyzing(true)
                .keywords(List.of())
                .seo(null)
                .feedback(null)
                .build();
    }
}
