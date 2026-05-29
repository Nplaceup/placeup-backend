package com.dontworry.core.domain.keyword.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class KeywordSearchVolumes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keywords_id", nullable = false)
    private Keywords keyword;

    @Column
    private Integer monthlySearchVolume;

    @Column
    private String competitionLevel;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static KeywordSearchVolumes build(Keywords keyword, Integer monthlySearchVolume, String competitionLevel) {
        return KeywordSearchVolumes.builder()
                .keyword(keyword)
                .monthlySearchVolume(monthlySearchVolume)
                .competitionLevel(competitionLevel)
                .build();
    }
}
