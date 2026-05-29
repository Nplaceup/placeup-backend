package com.dontworry.core.domain.keyword.entity;

import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "keyword_search_volume_crawling", indexes = {
        @Index(name = "idx_ksvc_status", columnList = "status"),
        @Index(name = "idx_ksvc_date", columnList = "crawl_date")
})
public class KeywordSearchVolumeCrawling {

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

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static KeywordSearchVolumeCrawling buildStarted(Keywords keyword, LocalDate crawlDate) {
        return KeywordSearchVolumeCrawling.builder()
                .keyword(keyword)
                .status(CrawlingStatus.PROCESSING)
                .crawlDate(crawlDate)
                .startedAt(LocalDateTime.now())
                .build();
    }
}
