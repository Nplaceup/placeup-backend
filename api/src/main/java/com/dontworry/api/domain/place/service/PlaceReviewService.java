package com.dontworry.api.domain.place.service;

import com.dontworry.api.controller.callback.dto.ReviewAnalysisItem;
import com.dontworry.api.controller.callback.dto.VisitorReviewItem;
import com.dontworry.api.domain.place.repository.PlaceReviewAnalysisRepository;
import com.dontworry.api.domain.place.repository.PlaceReviewRepository;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.api.domain.place.repository.ReviewAnalysisRepository;
import com.dontworry.core.domain.place.entity.PlaceReviewAnalysis;
import com.dontworry.core.domain.place.entity.PlaceReviews;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.place.entity.ReviewAnalysis;
import com.dontworry.core.domain.ranking.enums.ReviewAnalysisType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceReviewService {

    private final PlaceReviewRepository placeReviewRepository;
    private final ReviewAnalysisRepository reviewAnalysisRepository;
    private final PlaceReviewAnalysisRepository placeReviewAnalysisRepository;
    private final PlacesRepository placesRepository;

    @Transactional
    public void saveVisitorReviews(Long naverPlaceId, List<VisitorReviewItem> reviewItems) {
        Places place = placesRepository.findByNaverPlaceId(naverPlaceId);
        if (place == null) return;

        List<PlaceReviews> toSave = reviewItems.stream()
                .filter(item -> item.body() != null && !item.body().isBlank())
                .filter(item -> {
                    if (item.naverReviewId() != null) {
                        return !placeReviewRepository.existsByPlaceAndNaverReviewId(
                                place, item.naverReviewId());
                    }
                    return true;
                })
                .map(item -> PlaceReviews.builder()
                        .place(place)
                        .naverReviewId(item.naverReviewId())
                        .body(item.body())
                        .visited(parseVisited(item.visited()))
                        .build())
                .toList();

        placeReviewRepository.saveAll(toSave);
        log.info("[PlaceReviewService] placeId={} 리뷰 저장 {}건 (중복 제외)", naverPlaceId, toSave.size());
    }

    @Transactional
    public void upsertReviewAnalysis(
            Long naverPlaceId,
            LocalDateTime crawlDate,
            List<ReviewAnalysisItem> themes,
            List<ReviewAnalysisItem> menus) {

        Places place = placesRepository.findByNaverPlaceId(naverPlaceId);
        if (place == null) throw new IllegalArgumentException("Place not found: " + naverPlaceId);

        upsertAnalysisList(place, crawlDate, themes, ReviewAnalysisType.THEMES);
        upsertAnalysisList(place, crawlDate, menus, ReviewAnalysisType.MENUS);
    }

    @Transactional
    public void upsertAnalysisList(
            Places place,
            LocalDateTime crawlDate,
            List<ReviewAnalysisItem> items,
            ReviewAnalysisType type) {

        if (items == null || items.isEmpty()) return;

        List<String> labels = items.stream()
                .map(ReviewAnalysisItem::label)
                .toList();

        // 1. 기존 ReviewAnalysis 한 번에 조회
        Map<String, ReviewAnalysis> existingMap = reviewAnalysisRepository
                .findByTypeAndLabelIn(type, labels)
                .stream()
                .collect(Collectors.toMap(ReviewAnalysis::getLabel, ra -> ra));

        // 2. 없는 것만 추려서 벌크 insert
        List<ReviewAnalysis> toSave = labels.stream()
                .filter(label -> !existingMap.containsKey(label))
                .map(label -> ReviewAnalysis.builder()
                        .type(type)
                        .label(label)
                        .build())
                .toList();

        if (!toSave.isEmpty()) {
            List<ReviewAnalysis> saved = reviewAnalysisRepository.saveAll(toSave);
            saved.forEach(ra -> existingMap.put(ra.getLabel(), ra));
        }

        // 3. PlaceReviewAnalysis 벌크 insert
        List<PlaceReviewAnalysis> analyses = items.stream()
                .map(item -> PlaceReviewAnalysis.builder()
                        .place(place)
                        .reviewAnalysis(existingMap.get(item.label()))
                        .count(item.count())
                        .crawlDate(crawlDate)
                        .build())
                .toList();

        placeReviewAnalysisRepository.saveAll(analyses);
    }

    public String formatVisited(String visited) {
        LocalDate date = parseVisited(visited);
        return date != null ? date.toString() : null;
    }

    public LocalDate parseVisited(String visited) {
        if (visited == null || visited.isBlank()) return null;
        try {
            String[] parts = visited.split("\\.");

            int year, month, day;

            if (parts.length == 4) {
                year = 2000 + Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]);
                day = Integer.parseInt(parts[2]);
            } else if (parts.length == 3) {
                month = Integer.parseInt(parts[0]);
                day = Integer.parseInt(parts[1]);
                year = LocalDate.now().getYear();

                if (month > LocalDate.now().getMonthValue()) {
                    year -= 1;
                }
            } else {
                return null;
            }

            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            log.warn("[parseVisited] 파싱 실패: {}", visited);
            return null;
        }
    }
}