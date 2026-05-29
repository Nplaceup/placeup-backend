package com.dontworry.core.domain.keyword.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class KeywordRelated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keywords_id", nullable = false)
    private Keywords keyword;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer monthlySearchVolume;

    @Column
    private String competitionLevel;
}
