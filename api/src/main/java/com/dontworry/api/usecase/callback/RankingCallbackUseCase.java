package com.dontworry.api.usecase.callback;

import com.dontworry.api.controller.callback.dto.RankingCallbackResponse;
import com.dontworry.api.domain.ranking.service.RankingCrawlingService;
import com.dontworry.api.domain.keyword.service.KeywordService;
import com.dontworry.api.domain.place.service.PlaceService;
import com.dontworry.api.domain.ranking.service.RankingService;
import com.dontworry.api.infra.rabbitmq.dto.KeywordCrawlingEvent;
import com.dontworry.api.infra.rabbitmq.dto.PlaceCrawlingEvent;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.ranking.entity.Rankings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingCallbackUseCase {

    private final PlaceService placeService;
    private final KeywordService keywordService;
    private final RankingService rankingService;
    private final RankingCrawlingService rankingCrawlingService;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public void processRankingsCallback(String crawledKeyword, LocalDate crawlDate, List<RankingCallbackResponse.RankingResponse> responses) {
        if (crawledKeyword == null || crawlDate == null || responses == null || responses.isEmpty()) {
            if (crawledKeyword != null && crawlDate != null) {
                Keywords keyword = keywordService.getKeyword(crawledKeyword);
                rankingCrawlingService.updateRankingCrawlingStatus(keyword, crawlDate, CrawlingStatus.FAILED);
                log.warn("[Rankings Crawler Callback] 결과 없음 → FAILED keyword={}", crawledKeyword);
            } else {
                log.warn("[Rankings Crawler Callback] 결과 없음");
            }
            return;
        }

        Keywords keyword = keywordService.getKeyword(crawledKeyword);

        keyword.setTotalPlaceCount(responses.getFirst().total());

        List<Rankings> rankings = new ArrayList<>();
        for (RankingCallbackResponse.RankingResponse response : responses) {

            Places place = placeService.createOrUpdatePlacesWithDetail(response);

            Rankings rank = Rankings.builder()
                    .keyword(keyword)
                    .place(place)
                    .rankNo(response.rankNo())
                    .visitorReviewCount(response.visitorReviewCount())
                    .blogReviewCount(response.blogReviewCount())
                    .totalScore(response.totalScore())
                    .crawlDate(crawlDate)
                    .build();

            rankings.add(rank);
        }

        rankingService.saveAllRankings(rankings);
        rankingCrawlingService.updateRankingCrawlingStatus(keyword, crawlDate, CrawlingStatus.COMPLETED);

        triggerPlaceCrawling(crawlDate, responses);
        tiggerKeywordCraling(keyword);
    }

    public void tiggerKeywordCraling(Keywords keyword) {
        if(keyword.getKeywordSearchVolumes().isEmpty()){
            publisher.publishEvent(KeywordCrawlingEvent.builder()
                    .keyword(keyword)
                    .build());
        }
    }

    public void triggerPlaceCrawling(LocalDate crawlDate, List<RankingCallbackResponse.RankingResponse> responses) {
        if(isAllBlogCountsZero(responses)) {
            publisher.publishEvent(PlaceCrawlingEvent.builder()
                    .crawlDate(crawlDate)
                    .rankings(responses)
                    .build());
        }
    }

    private boolean isAllBlogCountsZero(List<RankingCallbackResponse.RankingResponse> responseList) {
        return responseList.stream()
                .allMatch(placeRawDto -> placeRawDto.blogReviewCount() == 0L);
    }

}
