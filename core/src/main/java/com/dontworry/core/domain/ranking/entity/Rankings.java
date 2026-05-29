package com.dontworry.core.domain.ranking.entity;

import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.place.entity.Places;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rankings", indexes = {
        @Index(name = "idx_rankings_date", columnList = "crawlDate")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Rankings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keywords keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Places place;

    @Column(nullable = false)
    private Integer rankNo;
    private Integer rankNoChange;

    private Integer visitorReviewCount;
    private Integer visitorReviewChange;

    private Integer blogReviewCount;
    private Integer blogReviewChange;

    private Double totalScore;
    private Double totalScoreChange;

    private LocalDate crawlDate;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
