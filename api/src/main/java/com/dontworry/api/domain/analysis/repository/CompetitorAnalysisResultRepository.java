package com.dontworry.api.domain.analysis.repository;

import com.dontworry.core.modeling.entity.CompetitorAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitorAnalysisResultRepository extends JpaRepository<CompetitorAnalysisResult, Long> {
    Optional<CompetitorAnalysisResult> findByPlaceId(Long placeId);
}
