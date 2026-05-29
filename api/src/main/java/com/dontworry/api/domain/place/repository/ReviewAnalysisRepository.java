package com.dontworry.api.domain.place.repository;

import com.dontworry.core.domain.place.entity.ReviewAnalysis;
import com.dontworry.core.domain.ranking.enums.ReviewAnalysisType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewAnalysisRepository extends JpaRepository<ReviewAnalysis, Long> {
    Optional<ReviewAnalysis> findByTypeAndLabel(ReviewAnalysisType type, String label);
    List<ReviewAnalysis> findByTypeAndLabelIn(ReviewAnalysisType type, List<String> labels);
}