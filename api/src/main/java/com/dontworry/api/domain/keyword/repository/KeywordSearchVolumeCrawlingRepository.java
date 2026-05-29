package com.dontworry.api.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumeCrawling;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface KeywordSearchVolumeCrawlingRepository extends JpaRepository<KeywordSearchVolumeCrawling, Long> {

    Optional<KeywordSearchVolumeCrawling> findByKeywordAndCrawlDate(Keywords keyword, LocalDate crawlDate);

    boolean existsByKeyword_IdAndCrawlDateAndStatus(Long keywordId, LocalDate crawlDate, CrawlingStatus status);
    Optional<KeywordSearchVolumeCrawling> findTopByKeyword_KeywordNameOrderByCrawlDateDesc(String keywordName);
}