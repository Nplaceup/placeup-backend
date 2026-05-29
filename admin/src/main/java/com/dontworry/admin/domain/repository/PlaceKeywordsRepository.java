package com.dontworry.admin.domain.repository;

import com.dontworry.core.domain.userPlaceKeyword.entity.UserPlaceKeywords;
import com.dontworry.core.domain.userPlaceKeyword.enums.PlaceKeywordStatus;
import com.dontworry.core.domain.userPlace.entity.UserPlaces;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceKeywordsRepository extends JpaRepository<UserPlaceKeywords, Long> {

    List<UserPlaceKeywords> findByUserPlacesIdInOrderByCreatedAtDesc(List<Long> userPlacesIds);
    List<UserPlaceKeywords> findAllByUserPlacesAndStatusOrderByCreatedAtDesc(UserPlaces userPlaces, PlaceKeywordStatus status);
}