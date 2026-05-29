package com.dontworry.api.domain.place.service;

import com.dontworry.api.controller.callback.dto.RankingCallbackResponse;
import com.dontworry.api.domain.place.repository.PlacesRepository;
import com.dontworry.api.infra.crawler.dto.PlaceInfoResponse;
import com.dontworry.api.infra.crawler.PlaceHtmlClient;
import com.dontworry.core.domain.place.entity.Places;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlacesRepository placesRepository;
    private final PlaceHtmlClient userPlacesHtmlDriverUtil;

    @Transactional
    public Places createOrUpdatePlaces(PlaceInfoResponse placeInfoResponse) {
        Places place = getPlacesByNaverId(placeInfoResponse.getNaverPlaceId());

        if (place == null) {
            Places newPlace = Places.builder()
                    .naverPlaceId(placeInfoResponse.getNaverPlaceId())
                    .placeName(placeInfoResponse.getNaverPlaceName())
                    .category(placeInfoResponse.getCategory())
                    .address(placeInfoResponse.getAddress())
                    .phoneNumber(placeInfoResponse.getPhoneNumber())
                    .talktalkUrl(placeInfoResponse.getTalktalkUrl())
                    .reservationAvailable(placeInfoResponse.getReservationAvailable())
                    .build();
            return placesRepository.save(newPlace);
        } else {
            place.update(
                    placeInfoResponse.getNaverPlaceName(),
                    placeInfoResponse.getCategory(),
                    placeInfoResponse.getAddress(),
                    placeInfoResponse.getPhoneNumber(),
                    placeInfoResponse.getTalktalkUrl(),
                    placeInfoResponse.getReservationAvailable()
            );
            return placesRepository.save(place);
        }
    }

    @Transactional
    public Places createOrUpdatePlacesWithDetail(RankingCallbackResponse.RankingResponse rankingResponse) {
        Places place = getPlacesByNaverId(rankingResponse.placeId());

        if (place == null) {
            Places newPlace = Places.buildWithDetails(
                    rankingResponse.placeId(),
                    rankingResponse.placeName(),
                    rankingResponse.category(),
                    rankingResponse.roadAddress(),
                    rankingResponse.url(),
                    rankingResponse.saveCount(),
                    rankingResponse.phoneNumber(),
                    rankingResponse.reservationAvailable(),
                    rankingResponse.naverPayAvailable(),
                    rankingResponse.talktalkUrl(),
                    rankingResponse.couponCount(),
                    rankingResponse.cidList());
            return placesRepository.save(newPlace);
        } else {
            place.updateWithDetails(
                    rankingResponse.placeName(),
                    rankingResponse.category(),
                    rankingResponse.roadAddress(),
                    rankingResponse.url(),
                    rankingResponse.saveCount(),
                    rankingResponse.phoneNumber(),
                    rankingResponse.reservationAvailable(),
                    rankingResponse.naverPayAvailable(),
                    rankingResponse.talktalkUrl(),
                    rankingResponse.couponCount(),
                    rankingResponse.cidList()
            );
            return placesRepository.save(place);
        }
    }

    @Transactional
    public Places getOrCreatePlaces(Long naverPlaceId, String naverPlaceUrl) {
        Places place = getPlacesByNaverId(naverPlaceId);
        if (place != null) return place;

        PlaceInfoResponse placeInfo = userPlacesHtmlDriverUtil.validateUserPlacesUrl(naverPlaceUrl);
        return createOrUpdatePlaces(placeInfo);
    }

    @Transactional
    public Places savePlaces(Places place) {
        return placesRepository.save(place);
    }

    @Transactional(readOnly = true)
    public Places getPlacesByNaverId(Long naverPlaceId) {
        return placesRepository.findByNaverPlaceId(naverPlaceId);
    }
}