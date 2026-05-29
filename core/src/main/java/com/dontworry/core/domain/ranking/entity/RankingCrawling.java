package com.dontworry.core.domain.ranking.entity;

import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ranking_crawling", indexes = {
        @Index(name = "idx_rc_status", columnList = "status"),
        @Index(name = "idx_rc_date", columnList = "crawl_date")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingCrawling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keywords_id", nullable = false)
    private Keywords keyword;

    @Column(name = "crawl_date", nullable = false)
    private LocalDate crawlDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CrawlingStatus status;

    private Integer workerId;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static RankingCrawling buildStarted(Keywords keyword, LocalDate crawlDate) {
        return RankingCrawling.builder()
                .keyword(keyword)
                .crawlDate(crawlDate)
                .status(CrawlingStatus.PROCESSING)
                .workerId(1)
                .startedAt(LocalDateTime.now())
                .build();
    }

    public static RankingCrawling build(Keywords keyword, LocalDate crawlDate, CrawlingStatus status) {
        return RankingCrawling.builder()
                .keyword(keyword)
                .crawlDate(crawlDate)
                .status(status)
                .workerId(1)
                .startedAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now())
                .build();
    }

    public void finishWithStatus(CrawlingStatus status) {
        this.status = status;
        this.finishedAt = LocalDateTime.now();
    }
}

