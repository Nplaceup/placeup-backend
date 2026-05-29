package com.dontworry.core.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PlaceReviews {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "place_reviews_seq")
    @SequenceGenerator(name = "place_reviews_seq", sequenceName = "place_reviews_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "places_id", nullable = false)
    private Places place;

    @Column
    private String naverReviewId;

    @Column(columnDefinition = "text")
    private String body;

    @Column
    private LocalDate visited;

    @CreatedDate
    private LocalDateTime createdAt;
}