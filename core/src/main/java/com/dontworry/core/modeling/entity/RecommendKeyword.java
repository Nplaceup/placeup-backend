package com.dontworry.core.modeling.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "recommend_keywords",
        uniqueConstraints = @UniqueConstraint(columnNames = {"place_id", "keyword"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Integer placeId;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private Double tfidfScore;

    @Column(nullable = false)
    private Double sentimentScore;

    @Column(nullable = false)
    private Double recencyScore;

    @Column(nullable = false)
    private Double consistencyScore;

    @Column(nullable = false)
    private Boolean isNgram;

    @Column(nullable = false)
    private Boolean isInduced;

    @Column(nullable = false, length = 20)
    private String keywordPurpose;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, length = 1)
    private String caseType;

    @Column
    private Integer rankNo;

    @Column(nullable = false)
    private Integer rankNoChange;

    @Column(nullable = false)
    private Integer monthlySearchVolume;

    @Column(nullable = false)
    private Integer mentionCount;

    @Column(nullable = false, length = 10)
    private String competitionLevel;

    @Column(nullable = false)
    private Boolean isOpportunity;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;
}