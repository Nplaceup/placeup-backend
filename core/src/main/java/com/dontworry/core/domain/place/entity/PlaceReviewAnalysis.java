package com.dontworry.core.domain.place.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "place_review_analysis", indexes = {
        @Index(name = "idx_pra_date", columnList = "crawl_date")
})
public class PlaceReviewAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pra_seq")
    @SequenceGenerator(name = "pra_seq", sequenceName = "place_review_analysis_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "places_id")
    private Places place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_analysis_id")
    private ReviewAnalysis reviewAnalysis;

    @Column
    private Integer count;

    @Column
    private LocalDateTime crawlDate;
}