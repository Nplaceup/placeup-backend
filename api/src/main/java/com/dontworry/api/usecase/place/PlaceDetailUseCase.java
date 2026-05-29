package com.dontworry.api.usecase.place;

import com.dontworry.api.controller.analysis.dto.PlaceDetailResponse;
import com.dontworry.api.controller.callback.dto.ReviewAnalysisItem;
import com.dontworry.api.controller.callback.dto.VisitorReviewItem;
import com.dontworry.api.infra.crawler.dto.PlaceInfoResponse;
import com.dontworry.api.domain.place.repository.PlaceCrawlingRepository;
import com.dontworry.api.domain.place.repository.PlaceReviewAnalysisRepository;
import com.dontworry.api.domain.place.repository.PlaceReviewRepository;
import com.dontworry.api.domain.place.service.PlaceCrawlingService;
import com.dontworry.api.domain.place.service.PlaceReviewService;
import com.dontworry.api.domain.place.service.PlaceService;
import com.dontworry.api.infra.crawler.PlaceHtmlClient;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import com.dontworry.core.domain.place.entity.PlaceReviewAnalysis;
import com.dontworry.core.domain.place.entity.PlaceReviews;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.ranking.enums.ReviewAnalysisType;
import com.dontworry.api.infra.crawler.PlaceCrawlerClient;
import com.dontworry.api.infra.crawler.dto.PlaceDetailCrawlerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceDetailUseCase {

    private final PlaceHtmlClient placeHtmlClient;
    private final PlaceService placeService;
    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceReviewAnalysisRepository placeReviewAnalysisRepository;
    private final PlaceReviewService placeReviewService;
    private final PlaceCrawlerClient placeCrawlerClient;
    private final PlaceCrawlingRepository placeCrawlingRepository;
    private final PlaceCrawlingService placeCrawlingService;

    @Transactional
    public PlaceDetailResponse getPlaceDetail(String url) {
        // 1. URL에서 placeId만 추출 (네이버 HTTP 요청 없음)
        Long naverPlaceId = placeHtmlClient.extractPlaceIdFromUrl(url);

        // 2. DB 조회
        Places place = placeService.getPlacesByNaverId(naverPlaceId);

        // 3. 크롤링 필요 여부 판단
        boolean needsCrawling = (place == null) || needsCrawling(place);

        List<PlaceDetailResponse.ReviewItem> reviews;
        List<PlaceDetailResponse.AnalysisItem> themes;
        List<PlaceDetailResponse.AnalysisItem> menus;
        Integer blogCafeReviewCount;

        if (!needsCrawling) {
            // 4-A. DB hit → 네이버 크롤링 없이 바로 DB 조회
            log.info("[PlaceDetail] DB hit, 크롤링 생략 naverPlaceId={}", naverPlaceId);

            blogCafeReviewCount = place.getBlogCafeReviewCount();

            List<PlaceReviews> dbReviews = placeReviewRepository.findByPlace(place);
            List<PlaceReviewAnalysis> dbThemes = placeReviewAnalysisRepository
                    .findLatestByPlaceAndType(place, ReviewAnalysisType.THEMES);
            List<PlaceReviewAnalysis> dbMenus = placeReviewAnalysisRepository
                    .findLatestByPlaceAndType(place, ReviewAnalysisType.MENUS);

            reviews = dbReviews.stream()
                    .map(r -> new PlaceDetailResponse.ReviewItem(
                            r.getBody(),
                            r.getVisited() != null ? r.getVisited().toString() : null))
                    .toList();
            themes = dbThemes.stream()
                    .map(a -> new PlaceDetailResponse.AnalysisItem(
                            a.getReviewAnalysis().getLabel(), a.getCount()))
                    .toList();
            menus = dbMenus.stream()
                    .map(a -> new PlaceDetailResponse.AnalysisItem(
                            a.getReviewAnalysis().getLabel(), a.getCount()))
                    .toList();

        } else {
            // 4-B. 크롤링 필요 → HTML 파싱 + place 저장/업데이트 + place-crawler 호출
            log.info("[PlaceDetail] 크롤링 필요, HTML 파싱 시작 naverPlaceId={}", naverPlaceId);
            PlaceInfoResponse placeInfo = placeHtmlClient.validateUserPlacesUrl(url);
            place = placeService.createOrUpdatePlaces(placeInfo);

            log.info("[PlaceDetail] place-crawler 동기 호출 naverPlaceId={}", naverPlaceId);
            PlaceDetailCrawlerResponse crawlerResponse = placeCrawlerClient.fetchDetail(naverPlaceId);

            if (crawlerResponse != null) {
                List<VisitorReviewItem> reviewItems = crawlerResponse.visitorReviews().stream()
                        .map(r -> new VisitorReviewItem(r.naverReviewId(), r.body(), r.visited()))
                        .toList();
                List<ReviewAnalysisItem> themeItems = crawlerResponse.themes().stream()
                        .map(t -> new ReviewAnalysisItem(t.label(), t.count()))
                        .toList();
                List<ReviewAnalysisItem> menuItems = crawlerResponse.menus().stream()
                        .map(m -> new ReviewAnalysisItem(m.label(), m.count()))
                        .toList();

                placeReviewService.saveVisitorReviews(naverPlaceId, reviewItems);
                placeReviewService.upsertReviewAnalysis(
                        naverPlaceId, LocalDateTime.now(), themeItems, menuItems);

                placeCrawlingService.updatePlaceCrawlingStatus(
                        place, LocalDate.now(), CrawlingStatus.COMPLETED);

                blogCafeReviewCount = crawlerResponse.blogCafeReviewCount();

                place.updateCrawlingData(blogCafeReviewCount, crawlerResponse.imageReviewCount(), crawlerResponse.newsPostCount(), crawlerResponse.description(), crawlerResponse.menuList());
                placeService.savePlaces(place);

                reviews = crawlerResponse.visitorReviews().stream()
                        .map(r -> new PlaceDetailResponse.ReviewItem(
                                r.body(),
                                placeReviewService.formatVisited(r.visited())))
                        .toList();
                themes = crawlerResponse.themes().stream()
                        .map(t -> new PlaceDetailResponse.AnalysisItem(t.label(), t.count()))
                        .toList();
                menus = crawlerResponse.menus().stream()
                        .map(m -> new PlaceDetailResponse.AnalysisItem(m.label(), m.count()))
                        .toList();
            } else {
                log.warn("[PlaceDetail] place-crawler 응답 null naverPlaceId={}", naverPlaceId);
                placeCrawlingService.updatePlaceCrawlingStatus(
                        place, LocalDate.now(), CrawlingStatus.FAILED);
                blogCafeReviewCount = null;
                reviews = List.of();
                themes  = List.of();
                menus   = List.of();
            }
        }

        return PlaceDetailResponse.builder()
                .naverPlaceId(place.getNaverPlaceId())
                .placeName(place.getPlaceName())
                .category(place.getCategory())
                .address(place.getAddress())
                .phoneNumber(place.getPhoneNumber())
                .placeUrl(place.getPlaceUrl())
                .saveCount(place.getSaveCount())
                .reservationAvailable(place.getReservationAvailable())
                .naverPayAvailable(place.getNaverPayAvailable())
                .talktalkUrl(place.getTalktalkUrl())
                .couponCount(place.getCouponCount())
                .blogCafeReviewCount(blogCafeReviewCount)
                .reviews(reviews)
                .themes(themes)
                .menus(menus)
                .build();
    }

    private boolean needsCrawling(Places place) {
        return placeCrawlingRepository
                .findTopByPlaceOrderByCrawlDateDesc(place)
                .map(crawling -> {
                    LocalDate lastCrawlDate = crawling.getCrawlDate();
                    LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
                    return lastCrawlDate.isBefore(thisMonth)
                            || crawling.getCrawlingStatus() != CrawlingStatus.COMPLETED;
                })
                .orElse(true);
    }
}