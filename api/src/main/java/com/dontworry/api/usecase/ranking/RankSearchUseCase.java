package com.dontworry.api.usecase.ranking;

import com.dontworry.api.domain.keyword.repository.KeywordSearchVolumeCrawlingRepository;
import com.dontworry.api.domain.keyword.repository.KeywordSearchVolumeRepository;
import com.dontworry.api.domain.keyword.repository.KeywordsRepository;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.api.domain.ranking.repository.RankingCrawlingRepository;
import com.dontworry.api.domain.ranking.repository.RankingRepository;
import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumeCrawling;
import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumes;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.ranking.entity.RankingCrawling;
import com.dontworry.core.domain.ranking.entity.Rankings;
import com.dontworry.api.infra.crawler.KeywordCrawlerClient;
import com.dontworry.api.infra.crawler.RankingCrawlerClient;
import com.dontworry.api.infra.crawler.dto.KeywordCrawlerResponse;
import com.dontworry.api.infra.crawler.dto.RankSearchResponse;
import com.dontworry.api.infra.crawler.dto.RankingCrawlerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dontworry.core.domain.keyword.enums.CrawlingOperation;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankSearchUseCase {

    private final RankingCrawlerClient rankingCrawlerClient;
    private final KeywordCrawlerClient keywordCrawlerClient;
    private final RankingCrawlingRepository rankingCrawlingRepository;
    private final KeywordSearchVolumeCrawlingRepository keywordSearchVolumeCrawlingRepository;
    private final KeywordsRepository keywordsRepository;
    private final RankingRepository rankingRepository;
    private final KeywordSearchVolumeRepository keywordSearchVolumeRepository;
    private final PlacesRepository placesRepository;

    @Transactional
    public RankSearchResponse getRankSearch(String keyword, Long naverPlaceId) {
        String normalizedKeyword = keyword.trim().replaceAll("\\s+", "");

        // 순위 데이터
        Integer rankNo = null;
        Integer totalPlaceCount = null;
        Integer visitorReviewCount = null;
        Integer blogReviewCount = null;
        Double totalScore = null;

        // 검색량 데이터
        Integer monthlySearchVolume = null;
        String competitionLevel = null;

        // Keywords 엔티티 조회 (없으면 자동 생성)
        Keywords keywordEntity = keywordsRepository.findByKeywordName(normalizedKeyword);
        if (keywordEntity == null) {
            log.info("[RankSearch] 신규 keyword 생성 keyword={}", normalizedKeyword);
            keywordEntity = keywordsRepository.save(
                    Keywords.builder()
                            .keywordName(normalizedKeyword)
                            .priority(0L)
                            // industry, location은 null (추후 추가 가능)
                            .build()
            );
        }

        // operation 결정 (industry 없으면 기본값 getPlacesList)
        CrawlingOperation operation = keywordEntity.getIndustry() != null
                && keywordEntity.getIndustry().getOperationName() != null
                ? keywordEntity.getIndustry().getOperationName()
                : CrawlingOperation.getPlacesList;

        // 1. 순위 조회
        boolean rankNeedsCrawling = needsRankingCrawling(normalizedKeyword);

        if (!rankNeedsCrawling) {
            // DB hit
            log.info("[RankSearch] 순위 DB hit keyword={}", normalizedKeyword);
            LocalDate baseDate = getBaseDate(normalizedKeyword);
            List<Rankings> rankings = rankingRepository
                    .findByKeyword_KeywordNameAndCrawlDate(normalizedKeyword, baseDate);

            totalPlaceCount = rankings.stream()
                    .findFirst()
                    .map(r -> r.getKeyword().getTotalPlaceCount() != null
                            ? r.getKeyword().getTotalPlaceCount().intValue() : null)
                    .orElse(null);

            Rankings myPlace = rankings.stream()
                    .filter(r -> naverPlaceId.equals(r.getPlace().getNaverPlaceId()))
                    .findFirst()
                    .orElse(null);

            if (myPlace != null) {
                rankNo             = myPlace.getRankNo();
                visitorReviewCount = myPlace.getVisitorReviewCount();
                blogReviewCount    = myPlace.getBlogReviewCount();
                totalScore         = myPlace.getTotalScore();
            }
        } else {
            // DB miss — ranking-crawler 호출
            log.info("[RankSearch] 순위 크롤링 필요 keyword={}, operation={}", normalizedKeyword, operation);
            RankingCrawlerResponse rankingResponse =
                    rankingCrawlerClient.fetchRanking(normalizedKeyword, operation);

            if (rankingResponse != null && rankingResponse.crawlerResponses() != null) {
                totalPlaceCount = rankingResponse.crawlerResponses().stream()
                        .findFirst()
                        .map(r -> r.total() != null ? r.total().intValue() : null)
                        .orElse(null);

                if (totalPlaceCount != null) {
                    keywordEntity.setTotalPlaceCount(totalPlaceCount.longValue());
                }

                for (RankingCrawlerResponse.RankingItem item : rankingResponse.crawlerResponses()) {
                    Places place = placesRepository.findByNaverPlaceId(item.placeId());
                    if (place == null) {
                        log.info("[RankSearch] place 없음 → 신규 저장 naverPlaceId={}", item.placeId());
                        place = placesRepository.save(
                                Places.build(
                                        item.placeId(),
                                        item.placeName(),
                                        item.category(),
                                        item.roadAddress()
                                )
                        );
                    }
                    rankingRepository.save(
                            Rankings.builder()
                                    .keyword(keywordEntity)
                                    .place(place)
                                    .rankNo(item.rankNo())
                                    .visitorReviewCount(item.visitorReviewCount())
                                    .blogReviewCount(item.blogReviewCount())
                                    .totalScore(item.totalScore())
                                    .crawlDate(LocalDate.now())
                                    .build()
                    );
                }

                // ranking_crawling COMPLETED 저장
                saveRankingCrawling(keywordEntity, CrawlingStatus.COMPLETED);

                // 요청한 placeId 순위 추출
                RankingCrawlerResponse.RankingItem myPlace = rankingResponse.crawlerResponses()
                        .stream()
                        .filter(r -> naverPlaceId.equals(r.placeId()))
                        .findFirst()
                        .orElse(null);

                if (myPlace != null) {
                    rankNo             = myPlace.rankNo();
                    visitorReviewCount = myPlace.visitorReviewCount();
                    blogReviewCount    = myPlace.blogReviewCount();
                    totalScore         = myPlace.totalScore();
                }
            } else {
                log.warn("[RankSearch] 순위 크롤링 응답 null keyword={}", normalizedKeyword);
                saveRankingCrawling(keywordEntity, CrawlingStatus.FAILED);
            }
        }

        // 2. 검색량 조회
        boolean keywordNeedsCrawling = needsKeywordCrawling(normalizedKeyword);

        if (!keywordNeedsCrawling) {
            // DB hit — keyword_search_volumes 테이블 조회
            log.info("[RankSearch] 검색량 DB hit keyword={}", normalizedKeyword);
            Optional<KeywordSearchVolumes> latestVolume = keywordSearchVolumeRepository
                    .findTopByKeywordOrderByCreatedAtDesc(keywordEntity);

            if (latestVolume.isPresent()) {
                monthlySearchVolume = latestVolume.get().getMonthlySearchVolume();
                competitionLevel    = latestVolume.get().getCompetitionLevel();
            }
        } else {
            // DB miss — keyword-crawler 호출
            log.info("[RankSearch] 검색량 크롤링 필요 keyword={}", normalizedKeyword);
            KeywordCrawlerResponse keywordResponse =
                    keywordCrawlerClient.fetchKeyword(normalizedKeyword);

            if (keywordResponse != null && keywordResponse.keywordList() != null) {
                KeywordCrawlerResponse.RelKwdStat mainStat = keywordResponse.keywordList().stream()
                        .filter(s -> normalizedKeyword.equals(s.relKeyword()))
                        .findFirst()
                        .orElse(keywordResponse.keywordList().isEmpty()
                                ? null : keywordResponse.keywordList().get(0));

                if (mainStat != null) {
                    monthlySearchVolume = mainStat.totalSearchVolume();
                    competitionLevel    = mainStat.compIdx();

                    // keyword_search_volumes 저장 (이력 누적)
                    keywordSearchVolumeRepository.save(
                            KeywordSearchVolumes.build(
                                    keywordEntity,
                                    mainStat.totalSearchVolume(),
                                    mainStat.compIdx()
                            )
                    );

                    saveKeywordCrawling(keywordEntity, CrawlingStatus.COMPLETED);
                }
            } else {
                log.warn("[RankSearch] 검색량 크롤링 응답 null keyword={}", normalizedKeyword);
                saveKeywordCrawling(keywordEntity, CrawlingStatus.FAILED);
            }
        }

        return RankSearchResponse.builder()
                .keyword(normalizedKeyword)
                .naverPlaceId(naverPlaceId)
                .rankNo(rankNo)
                .totalPlaceCount(totalPlaceCount)
                .visitorReviewCount(visitorReviewCount)
                .blogReviewCount(blogReviewCount)
                .totalScore(totalScore)
                .monthlySearchVolume(monthlySearchVolume)
                .competitionLevel(competitionLevel)
                .build();
    }

    // 오늘 날짜 기준 ranking_crawling COMPLETED 이력 없으면 크롤링 필요
    private boolean needsRankingCrawling(String keyword) {
        return !rankingCrawlingRepository
                .existsByKeyword_KeywordNameAndCrawlDateAndStatus(
                        keyword, LocalDate.now(), CrawlingStatus.COMPLETED);
    }

    // 이번 달 기준 keyword_search_volume_crawling COMPLETED 이력 없으면 크롤링 필요
    private boolean needsKeywordCrawling(String keyword) {
        return keywordSearchVolumeCrawlingRepository
                .findTopByKeyword_KeywordNameOrderByCrawlDateDesc(keyword)
                .map(crawling -> {
                    LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
                    return crawling.getCrawlDate().isBefore(thisMonth)
                            || crawling.getStatus() != CrawlingStatus.COMPLETED;
                })
                .orElse(true);
    }

    // ranking 조회 기준 날짜 (오늘 데이터 없으면 어제)
    private LocalDate getBaseDate(String keyword) {
        boolean hasTodayData = rankingCrawlingRepository
                .existsByKeyword_KeywordNameAndCrawlDateAndStatus(
                        keyword, LocalDate.now(), CrawlingStatus.COMPLETED);
        return hasTodayData ? LocalDate.now() : LocalDate.now().minusDays(1);
    }

    private void saveRankingCrawling(Keywords keyword, CrawlingStatus status) {
        rankingCrawlingRepository.findByKeywordAndCrawlDate(keyword, LocalDate.now())
                .ifPresentOrElse(
                        crawling -> {
                            crawling.setStatus(status);
                            crawling.setFinishedAt(LocalDateTime.now());
                        },
                        () -> rankingCrawlingRepository.save(
                                RankingCrawling.builder()
                                        .keyword(keyword)
                                        .crawlDate(LocalDate.now())
                                        .status(status)
                                        .startedAt(LocalDateTime.now())
                                        .finishedAt(LocalDateTime.now())
                                        .build()
                        )
                );
    }

    private void saveKeywordCrawling(Keywords keyword, CrawlingStatus status) {
        keywordSearchVolumeCrawlingRepository
                .findByKeywordAndCrawlDate(keyword, LocalDate.now())
                .ifPresentOrElse(
                        crawling -> {
                            crawling.setStatus(status);
                            crawling.setFinishedAt(LocalDateTime.now());
                        },
                        () -> keywordSearchVolumeCrawlingRepository.save(
                                KeywordSearchVolumeCrawling.builder()
                                        .keyword(keyword)
                                        .crawlDate(LocalDate.now())
                                        .status(status)
                                        .startedAt(LocalDateTime.now())
                                        .finishedAt(LocalDateTime.now())
                                        .build()
                        )
                );
    }
}