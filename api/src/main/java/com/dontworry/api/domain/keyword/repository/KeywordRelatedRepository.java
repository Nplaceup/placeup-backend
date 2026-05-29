package com.dontworry.api.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordRelated;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRelatedRepository extends JpaRepository<KeywordRelated, Long> {
}