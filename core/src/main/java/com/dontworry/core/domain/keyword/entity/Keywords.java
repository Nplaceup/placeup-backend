package com.dontworry.core.domain.keyword.entity;

import com.dontworry.core.domain.ranking.entity.RankingCrawling;
import com.dontworry.core.domain.ranking.entity.Rankings;
import com.dontworry.core.domain.userPlaceKeyword.entity.UserPlaceKeywords;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "keywords", indexes = {
        @Index(name = "idx_keywords_priority", columnList = "priority")})
@EntityListeners(AuditingEntityListener.class)
public class Keywords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keywordName;

    @Column(nullable = false)
    private Long priority;

    @Column
    private Long totalPlaceCount;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private KeywordIndustry industry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private KeywordLocation location;

    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<RankingCrawling> rankingCrawlings;

    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<UserPlaceKeywords> userPlaceKeywords;

    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<KeywordSearchVolumes> keywordSearchVolumes;

    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<KeywordRelated> keywordRelated;

    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<Rankings> rankings;

    public static Keywords build(String keywordName, KeywordIndustry industry, KeywordLocation location) {
        return Keywords.builder()
                .keywordName(keywordName)
                .priority(0L)
                .industry(industry)
                .location(location)
                .build();
    }

    public void setPriorityUserRegistered() {
        this.priority = 0L;
    }

    public LocalDate decideCrawlDate(boolean hasTodayCompleted) {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDate today = LocalDate.now(KST);
        LocalDate yesterday = today.minusDays(1);
        LocalTime now = LocalTime.now(KST);
        LocalTime cutoffTime = LocalTime.of(14, 0);

        if (now.isBefore(cutoffTime)) {
            return yesterday;
        }

        return hasTodayCompleted ? null : today;
    }
}
