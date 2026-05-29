package com.dontworry.scheduler.domain.ranking.repository;


import com.dontworry.core.domain.ranking.entity.RankingCrawling;

import java.time.LocalDate;
import java.util.List;

public interface RankingCrawlingCustomRepository {

    List<RankingCrawling> createAndGetTodayCrawlingList(LocalDate today, List<Long> priorities);
}
