package com.dontworry.api.controller.analysis.dto;

import com.dontworry.core.domain.place.entity.Places;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PlaceAnalysisResponse {

    private Long    naverPlaceId;
    private String  placeName;
    private boolean analyzing;

    // 분석 완료 응답
    public static PlaceAnalysisResponse of(Places place) {
        return PlaceAnalysisResponse.builder()
                .naverPlaceId(place.getNaverPlaceId())
                .placeName(place.getPlaceName())
                .analyzing(false)
                .build();
    }

    // 분석 중 응답
    public static PlaceAnalysisResponse analyzing(Places place) {
        return PlaceAnalysisResponse.builder()
                .naverPlaceId(place.getNaverPlaceId())
                .placeName(place.getPlaceName())
                .analyzing(true)
                .build();
    }
}