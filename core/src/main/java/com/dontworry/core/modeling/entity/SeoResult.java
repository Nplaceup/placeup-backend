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

    @Column(nullable = false)
    private Double keywordOptimization;

    @Column(nullable = false)
    private Double reviewQuality;

    @Column(nullable = false)
    private Double searchExposure;

    @Column(nullable = false)
    private Double competition;

    @Column(nullable = false, columnDefinition = "text")
    private String summary;

    // JSON 배열 문자열로 저장
    @Column(nullable = false, columnDefinition = "text")
    private String seoFeedback;

    // JSON 배열 문자열로 저장
    @Column(nullable = false, columnDefinition = "text")
    private String reviewFeedback;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;
}