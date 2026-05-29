package com.dontworry.api.domain.place.repository;

import com.dontworry.core.domain.place.entity.PlaceReviews;
import com.dontworry.core.domain.place.entity.Places;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewRepository extends JpaRepository<PlaceReviews, Long> {

    List<PlaceReviews> findByPlace(Places place);
    boolean existsByPlaceAndNaverReviewId(Places place, String naverReviewId);
}