package com.dontworry.api.domain.ranking.repository;

import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.ranking.entity.Rankings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Rankings, Long> {

    List<Rankings> findByPlace_NaverPlaceIdAndCrawlDate(final Long naverPlaceId, final LocalDate crawlDate);
    List<Rankings> findByKeyword_KeywordNameAndCrawlDate(String keywordName, LocalDate crawlDate);
    Optional<Rankings> findTopByPlaceOrderByCrawlDateDesc(Places place);
}
