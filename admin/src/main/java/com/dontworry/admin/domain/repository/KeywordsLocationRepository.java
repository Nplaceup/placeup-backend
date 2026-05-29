package com.dontworry.admin.domain.repository;

import com.dontworry.core.domain.keyword.entity.KeywordLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordsLocationRepository extends JpaRepository<KeywordLocation, Long> {
    KeywordLocation findByValue(String value);
}