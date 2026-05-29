package com.dontworry.scheduler.domain.place.repository;

import com.dontworry.core.domain.place.entity.PlaceCrawling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceCrawlingRepository extends JpaRepository<PlaceCrawling, Long>, PlaceCrawlingRepositoryCustom {
}
