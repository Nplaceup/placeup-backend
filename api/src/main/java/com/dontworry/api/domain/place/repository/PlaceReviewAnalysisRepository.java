package com.dontworry.api.domain.place.repository;

import com.dontworry.core.domain.place.entity.PlaceReviewAnalysis;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.ranking.enums.ReviewAnalysisType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewAnalysisRepository
        extends JpaRepository<PlaceReviewAnalysis, Long>, PlaceReviewAnalysisRepositoryCustom {
}