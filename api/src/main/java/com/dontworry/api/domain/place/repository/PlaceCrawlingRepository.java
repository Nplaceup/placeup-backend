package com.dontworry.api.domain.place.repository;

import com.dontworry.core.domain.place.entity.PlaceCrawling;
import com.dontworry.core.domain.place.entity.Places;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PlaceCrawlingRepository extends JpaRepository<PlaceCrawling, Long> {

    Optional<PlaceCrawling> findByPlaceAndCrawlDate(Places place, LocalDate crawlDate);
    Optional<PlaceCrawling> findTopByPlaceOrderByCrawlDateDesc(Places place);
}
