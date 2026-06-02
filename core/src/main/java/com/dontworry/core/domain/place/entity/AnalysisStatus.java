package com.dontworry.core.domain.place.entity;

import com.dontworry.core.domain.place.enums.AnalysisStatusType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AnalysisStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long placeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatusType status;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static AnalysisStatus of(Long placeId, AnalysisStatusType status) {
        return AnalysisStatus.builder()
                .placeId(placeId)
                .status(status)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateStatus(AnalysisStatusType status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}
