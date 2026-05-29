package com.dontworry.scheduler.domain.place.repository;

import com.dontworry.core.domain.place.entity.PlaceCrawling;

import java.time.LocalDate;
import java.util.List;

public interface PlaceCrawlingRepositoryCustom {
    List<PlaceCrawling> createAndGetThisMonthCrawlingList(LocalDate today);
}
