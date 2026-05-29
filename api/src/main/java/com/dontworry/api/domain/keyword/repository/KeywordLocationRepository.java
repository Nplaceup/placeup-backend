package com.dontworry.api.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordLocationRepository extends JpaRepository<KeywordLocation, Long> {

    KeywordLocation findByValue(String value);

    List<KeywordLocation> findAllByOrderByValueAsc();
}