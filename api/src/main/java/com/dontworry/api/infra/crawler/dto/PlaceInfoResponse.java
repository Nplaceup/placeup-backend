package com.dontworry.api.infra.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInfoResponse {

    private Long naverPlaceId;
    private String naverPlaceName;
    private String category;
    private String address;
    private String phoneNumber;
    private String roadAddress;
    private String talktalkUrl;
    private Boolean reservationAvailable;

    public static PlaceInfoResponse toDto(
            Long placeId,
            String placeName,
            String category,
            String address,
            String phoneNumber,
            String roadAddress,
            String talktalkUrl,
            Boolean reservationAvailable) {
        return PlaceInfoResponse.builder()
                .naverPlaceId(placeId)
                .naverPlaceName(placeName)
                .category(category)
                .address(address)
                .phoneNumber(phoneNumber)
                .roadAddress(roadAddress)
                .talktalkUrl(talktalkUrl)
                .reservationAvailable(reservationAvailable)
                .build();
    }
}
