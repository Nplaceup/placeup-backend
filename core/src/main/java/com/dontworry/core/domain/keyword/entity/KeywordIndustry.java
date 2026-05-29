package com.dontworry.core.domain.keyword.entity;

import com.dontworry.core.domain.keyword.enums.CrawlingOperation;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "keyword_industry")
@EntityListeners(AuditingEntityListener.class)
public class KeywordIndustry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    private String category;
    
    private Long priority;

    @Enumerated(EnumType.STRING)
    private CrawlingOperation operationName;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
