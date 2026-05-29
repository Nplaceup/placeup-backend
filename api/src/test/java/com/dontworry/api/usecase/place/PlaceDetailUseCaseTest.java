package com.dontworry.api.usecase.place;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class PlaceDetailUseCaseTest {

    @Autowired
    private PlaceDetailUseCase placeDetailUseCase;

    @Test
    void getPlaceDetail_실제호출_DB저장() {
        // given
        String url = "https://map.naver.com/p/entry/place/36333003";

        // when
        var result = placeDetailUseCase.getPlaceDetail(url);

        // then
        System.out.println("naverPlaceId: " + result.naverPlaceId());
        System.out.println("placeName: " + result.placeName());
        System.out.println("reviews: " + result.reviews().size());
        System.out.println("themes: " + result.themes().size());
        System.out.println("menus: " + result.menus().size());
    }
}