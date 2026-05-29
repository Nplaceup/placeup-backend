package com.dontworry.api.domain.place.repository;

import com.dontworry.core.domain.place.entity.PlaceReviewAnalysis;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.ranking.enums.ReviewAnalysisType;

import java.util.List;

public interface PlaceReviewAnalysisRepositoryCustom {

    List<PlaceReviewAnalysis> findLatestByPlaceAndType(Places place, ReviewAnalysisType type);
}