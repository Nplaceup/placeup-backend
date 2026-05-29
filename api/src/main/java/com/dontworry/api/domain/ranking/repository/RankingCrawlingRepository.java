package com.dontworry.api.domain.ranking.repository;

import com.dontworry.core.domain.ranking.entity.RankingCrawling;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RankingCrawlingRepository extends JpaRepository<RankingCrawling, Long> {

    boolean existsByKeyword_IdAndCrawlDateAndStatus(Long keywordId, LocalDate crawlDate, CrawlingStatus status);
    Optional<RankingCrawling> findByKeywordAndCrawlDate(Keywords keyword, LocalDate crawlDate);
    boolean existsByKeyword_KeywordNameAndCrawlDateAndStatus(String keywordName, LocalDate crawlDate, CrawlingStatus status);

}
