package com.dontworry.scheduler.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumeCrawling;

import java.time.LocalDate;
import java.util.List;

public interface KeywordSearchVolumeCrawlingRepositoryCustom {
    List<KeywordSearchVolumeCrawling> createAndGetThisMonthCrawlingList(LocalDate crawlingDate);
}
