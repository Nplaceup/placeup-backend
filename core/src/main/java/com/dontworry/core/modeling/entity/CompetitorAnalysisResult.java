package com.dontworry.core.modeling.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "competitor_analysis",
        uniqueConstraints = @UniqueConstraint(columnNames = {"place_id"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CompetitorAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "competitor_count", nullable = false)
    private Integer competitorCount;

    @Column(name = "competitor_names", nullable = false, columnDefinition = "text")
    private String competitorNames;

    @Column(name = "gap_keywords", nullable = false, columnDefinition = "text")
    private String gapKeywords;

    @Column(name = "rank_gap_keywords", nullable = false, columnDefinition = "text")
    private String rankGapKeywords;

    @Column(name = "advantage_keywords", nullable = false, columnDefinition = "text")
    private String advantageKeywords;

    @Column(name = "category_gap", nullable = false, columnDefinition = "text")
    private String categoryGap;

    @CreatedDate
    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;
}