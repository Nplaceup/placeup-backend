package com.dontworry.scheduler.domain.ranking.repository;

import com.dontworry.core.domain.ranking.entity.RankingCrawling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingCrawlingRepository extends JpaRepository<RankingCrawling, Long>, RankingCrawlingCustomRepository {
}
