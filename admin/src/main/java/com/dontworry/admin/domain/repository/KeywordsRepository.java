package com.dontworry.admin.domain.repository;

import com.dontworry.core.domain.keyword.entity.Keywords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordsRepository extends JpaRepository<Keywords, Long> {
    Keywords findByKeywordName(String keywordName);
}

