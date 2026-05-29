package com.dontworry.scheduler.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumeCrawling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordSearchVolumeCrawlingRepository
        extends JpaRepository<KeywordSearchVolumeCrawling, Long>, KeywordSearchVolumeCrawlingRepositoryCustom {
}
