package com.dontworry.api.domain.ranking.service;

import com.dontworry.api.domain.ranking.repository.RankingRepository;
import com.dontworry.core.domain.ranking.entity.Rankings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    @Transactional
    public void updateRankingsWithDetail(Long placeId, LocalDate crawlDate, Integer blogCafeReviewCount) {
        log.info("[RANKING-UPDATE-START] placeId={}, crawlDate={}, newValue={}", placeId, crawlDate, blogCafeReviewCount);
        List<Rankings> rankings = getRankingsByPlaceIdAndCrawlDate(placeId, crawlDate);
        rankings.forEach(ranking -> {
            log.info("[RANKING-UPDATE] place={}", ranking.getPlace().getNaverPlaceId());
            ranking.setBlogReviewCount(blogCafeReviewCount);
        });
        saveAllRankings(rankings);
        log.info("[RANKING-UPDATE-END] placeId={}, crawlDate={}, newValue={}", placeId, crawlDate, blogCafeReviewCount);
    }

    @Transactional
    public void saveAllRankings(List<Rankings> keywordPlaceRanks) {
        rankingRepository.saveAll(keywordPlaceRanks);
    }

    @Transactional
    public List<Rankings> getRankingsByPlaceIdAndCrawlDate(Long placeId, LocalDate crawlDate) {
        return rankingRepository.findByPlace_NaverPlaceIdAndCrawlDate(placeId, crawlDate);
    }
}