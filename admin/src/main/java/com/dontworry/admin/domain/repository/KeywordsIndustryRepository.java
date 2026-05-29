package com.dontworry.admin.domain.repository;

import com.dontworry.core.domain.keyword.entity.KeywordIndustry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordsIndustryRepository extends JpaRepository<KeywordIndustry, Long> {
    KeywordIndustry findByValue(String value);
}
