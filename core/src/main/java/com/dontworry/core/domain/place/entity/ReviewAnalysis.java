package com.dontworry.core.domain.place.entity;

import com.dontworry.core.domain.ranking.enums.ReviewAnalysisType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_analysis_seq")
    @SequenceGenerator(name = "review_analysis_seq", sequenceName = "review_analysis_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewAnalysisType type;

    @Column(nullable = false)
    private String label;

    @OneToMany(mappedBy = "reviewAnalysis", fetch = FetchType.LAZY)
    private List<PlaceReviewAnalysis> placeReviewAnalysis;
}