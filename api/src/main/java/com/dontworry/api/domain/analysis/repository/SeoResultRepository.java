package com.dontworry.api.domain.analysis.repository;

import com.dontworry.core.modeling.entity.SeoResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeoResultRepository extends JpaRepository<SeoResult, Long> {
    Optional<SeoResult> findByPlaceId(Long placeId);
}