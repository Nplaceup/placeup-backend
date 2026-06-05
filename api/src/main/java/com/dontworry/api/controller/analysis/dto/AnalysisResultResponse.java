package com.dontworry.api.controller.analysis.dto;

import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.modeling.entity.CompetitorAnalysisResult;
import com.dontworry.core.modeling.entity.RecommendKeyword;
import com.dontworry.core.modeling.entity.SeoResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Builder
public class AnalysisResultResponse {

    private Long    naverPlaceId;
    private String  placeName;
    private boolean analyzing;

    private List<KeywordItem> keywords;
    private SeoItem           seo;
    private FeedbackItem      feedback;
    private CompetitorItem    competitor;

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
        private Double  placeCompleteness;
        private Double  reviewQuality;
    }

    @Getter
    @Builder
    public static class FeedbackItem {
        private String                    summary;
        private List<String>              seoFeedback;
        private List<String>              reviewFeedback;
        private List<String>              competitorFeedback;
        private Map<String, List<String>> placeSummary;
    }

    @Getter
    @Builder
    public static class CompetitorItem {
        private Integer                       count;
        private List<String>                  names;
        private List<GapKeywordItem>          gapKeywords;
        private List<RankGapKeywordItem>      rankGapKeywords;
        private List<AdvantageKeywordItem>    advantageKeywords;
        private Map<String, CategoryGapValue> categoryGap;
    }

    @Getter
    @Builder
    public static class GapKeywordItem {
        private String  keyword;
        private String  category;
        private Integer monthlySearchVolume;
        private Integer competitorCount;
    }

    @Getter
    @Builder
    public static class RankGapKeywordItem {
        private String  keyword;
        private String  category;
        private Integer monthlySearchVolume;
        private Integer myRankNo;           // null = 70위 초과
        private Double  competitorAvgRank;
        private Double  rankGap;
    }

    @Getter
    @Builder
    public static class AdvantageKeywordItem {
        private String  keyword;
        private Integer monthlySearchVolume;
    }

    @Getter
    @Builder
    public static class CategoryGapValue {
        private Integer mine;
        private Double  competitorAvg;
    }

    // ── 공용 ObjectMapper (매 호출마다 생성 방지) ─────────────────────────
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ── 팩토리 메서드 ─────────────────────────────────────────────────────

    // 분석 완료
    public static AnalysisResultResponse of(
            Places place,
            List<RecommendKeyword> keywords,
            SeoResult seoResult,
            CompetitorAnalysisResult competitorResult) {

        // ── SEO ────────────────────────────────────────────────────────
        SeoItem      seoItem      = null;
        FeedbackItem feedbackItem = null;

        if (seoResult != null) {
            seoItem = SeoItem.builder()
                    .score(seoResult.getScore())
                    .grade(seoResult.getGrade())
                    .placeCompleteness(seoResult.getPlaceCompleteness())
                    .reviewQuality(seoResult.getReviewQuality())
                    .build();

            List<String> seoFeedback;
            List<String> reviewFeedback;
            List<String> competitorFeedback;
            Map<String, List<String>> placeSummary;
            try {
                seoFeedback        = MAPPER.readValue(seoResult.getSeoFeedback(),
                        new TypeReference<List<String>>() {});
                reviewFeedback     = MAPPER.readValue(seoResult.getReviewFeedback(),
                        new TypeReference<List<String>>() {});
                competitorFeedback = MAPPER.readValue(seoResult.getCompetitorFeedback(),
                        new TypeReference<List<String>>() {});
                placeSummary       = MAPPER.readValue(seoResult.getPlaceSummary(),
                        new TypeReference<Map<String, List<String>>>() {});
            } catch (Exception e) {
                log.error("[AnalysisResultResponse] feedback JSON 파싱 실패 naverPlaceId={}, error={}",
                        place.getNaverPlaceId(), e.getMessage());
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

        // ── Competitor ─────────────────────────────────────────────────
        CompetitorItem competitorItem = null;

        if (competitorResult != null) {
            try {
                competitorItem = CompetitorItem.builder()
                        .count(competitorResult.getCompetitorCount())
                        .names(MAPPER.readValue(
                                competitorResult.getCompetitorNames(),
                                new TypeReference<List<String>>() {}))
                        .gapKeywords(MAPPER.readValue(
                                competitorResult.getGapKeywords(),
                                new TypeReference<List<GapKeywordItem>>() {}))
                        .rankGapKeywords(MAPPER.readValue(
                                competitorResult.getRankGapKeywords(),
                                new TypeReference<List<RankGapKeywordItem>>() {}))
                        .advantageKeywords(MAPPER.readValue(
                                competitorResult.getAdvantageKeywords(),
                                new TypeReference<List<AdvantageKeywordItem>>() {}))
                        .categoryGap(MAPPER.readValue(
                                competitorResult.getCategoryGap(),
                                new TypeReference<Map<String, CategoryGapValue>>() {}))
                        .build();
            } catch (Exception e) {
                log.error("[AnalysisResultResponse] competitor JSON 파싱 실패 naverPlaceId={}, error={}",
                        place.getNaverPlaceId(), e.getMessage());
                competitorItem = null;
            }
        }

        // ── 최종 빌드 ──────────────────────────────────────────────────
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
                .competitor(competitorItem)
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
                .competitor(null)
                .build();
    }
}