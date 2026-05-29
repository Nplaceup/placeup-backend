package com.dontworry.core.domain.place.entity;

import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "place_crawling")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceCrawling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "places_id", nullable = false)
    private Places place;

    @Column(name = "crawl_date", nullable = false)
    private LocalDate crawlDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CrawlingStatus crawlingStatus;

    private Integer workerId;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static PlaceCrawling buildStarted(Places place, LocalDate crawlDate) {
        return PlaceCrawling.builder()
                .place(place)
                .crawlDate(crawlDate)
                .crawlingStatus(CrawlingStatus.PROCESSING)
                .workerId(1)
                .startedAt(LocalDateTime.now())
                .build();
    }

    public static PlaceCrawling build(Places place, LocalDate crawlDate, CrawlingStatus status) {
        return PlaceCrawling.builder()
                .place(place)
                .crawlDate(crawlDate)
                .crawlingStatus(status)
                .workerId(1)
                .startedAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now())
                .build();
    }

    public void finishWithStatus(CrawlingStatus status) {
        this.crawlingStatus = status;
        this.finishedAt = LocalDateTime.now();
    }
}
