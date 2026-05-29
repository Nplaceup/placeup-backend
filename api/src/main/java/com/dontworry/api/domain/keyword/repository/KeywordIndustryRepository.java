package com.dontworry.api.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordIndustry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordIndustryRepository extends JpaRepository<KeywordIndustry, Long> {

    KeywordIndustry findByValue(String value);

    List<KeywordIndustry> findAllByOrderByValueAsc();

}