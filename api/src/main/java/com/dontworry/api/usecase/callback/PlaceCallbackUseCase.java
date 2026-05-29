package com.dontworry.api.usecase.callback;

import com.dontworry.api.controller.callback.dto.PlaceCallbackResponse;
import com.dontworry.api.domain.place.service.PlaceCrawlingService;
import com.dontworry.api.domain.place.service.PlaceReviewService;
import com.dontworry.api.domain.place.service.PlaceService;
import com.dontworry.api.domain.ranking.service.RankingService;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import com.dontworry.core.domain.place.entity.Places;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceCallbackUseCase {

    private final RankingService rankingService;
    private final PlaceService placeService;
    private final PlaceReviewService placeReviewService;
    private final PlaceCrawlingService placeCrawlingService;

    @Transactional
    public void processPlacesCallback(PlaceCallbackResponse res) {
        // 응답 없음 필터
        if (res == null || res.placeId() == null || res.crawlDate() == null) {
            log.warn("[Places Crawler Callback] 결과 없음");
            return;
        }

        log.info("[Places Crawler Callback] start placeId={}", res.placeId());

        LocalDateTime crawlDateTime = res.crawlDate().atStartOfDay();

        // 실패 상태 필터
        if (res.blogCafeReviewCount() == null
                && (res.visitorReviews() == null || res.visitorReviews().isEmpty())
                && (res.themes() == null || res.themes().isEmpty())
                && (res.menus() == null || res.menus().isEmpty())) {
            Places place = placeService.getPlacesByNaverId(res.placeId());
            placeCrawlingService.updatePlaceCrawlingStatus(place, res.crawlDate(), CrawlingStatus.FAILED);
            log.warn("[Places Crawler Callback] 결과 없음 → FAILED placeId={}", res.placeId());
            return;
        }

        // 0. 크롤링 상태 변경 (COMPLETED)
        Places place = placeService.getPlacesByNaverId(res.placeId());
        placeCrawlingService.updatePlaceCrawlingStatus(place, res.crawlDate(), CrawlingStatus.COMPLETED);

        // 1. 블로그 리뷰수 → rankings 업데이트
        rankingService.updateRankingsWithDetail(
                res.placeId(), res.crawlDate(), res.blogCafeReviewCount());

        // 2. places.blog_cafe_review_count 업데이트 (단일 출처)
        place.updateCrawlingData(res.blogCafeReviewCount(), res.imageReviewCount(), res.newsPostCount(), res.description(), res.menuList());
        placeService.savePlaces(place);

        // 3. 방문자 리뷰 저장
        placeReviewService.saveVisitorReviews(
                res.placeId(), res.visitorReviews());

        // 4. 테마/메뉴 분석 upsert
        placeReviewService.upsertReviewAnalysis(
                res.placeId(), crawlDateTime,
                res.themes(), res.menus());
    }
}