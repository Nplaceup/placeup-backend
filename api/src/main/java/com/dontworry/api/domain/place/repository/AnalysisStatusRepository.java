package com.dontworry.api.domain.place.repository;

import com.dontworry.core.domain.place.entity.AnalysisStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisStatusRepository extends JpaRepository<AnalysisStatus, Long> {

    Optional<AnalysisStatus> findByPlaceId(Long placeId);
}
