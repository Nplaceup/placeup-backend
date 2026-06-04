package com.dontworry.core.modeling.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "seo_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"place_id"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SeoResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, length = 20)
    private String grade;

    // Python place_scorer breakdown: place_completeness / review_quality
    // (구 keywordOptimization, searchExposure, competition 제거됨)
    @Column(nullable = false)
    private Double placeCompleteness;

    @Column(nullable = false)
    private Double reviewQuality;

    // 카테고리별 대표 키워드 요약 — JSON 객체 문자열 {"음식": ["삼겹살", ...], ...}
    @Column(nullable = false, columnDefinition = "text")
    private String placeSummary;

    // 총평 한 줄 텍스트
    @Column(nullable = false, columnDefinition = "text")
    private String summary;

    // JSON 배열 문자열로 저장
    @Column(nullable = false, columnDefinition = "text")
    private String seoFeedback;

    // JSON 배열 문자열로 저장
    @Column(nullable = false, columnDefinition = "text")
    private String reviewFeedback;

    // JSON 배열 문자열로 저장
    @Column(nullable = false, columnDefinition = "text")
    private String competitorFeedback;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
