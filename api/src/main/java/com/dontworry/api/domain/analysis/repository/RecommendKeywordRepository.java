package com.dontworry.api.domain.analysis.repository;

import com.dontworry.core.modeling.entity.RecommendKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendKeywordRepository extends JpaRepository<RecommendKeyword, Long> {

    List<RecommendKeyword> findByPlaceIdOrderByScoreDesc(Integer placeId);
}