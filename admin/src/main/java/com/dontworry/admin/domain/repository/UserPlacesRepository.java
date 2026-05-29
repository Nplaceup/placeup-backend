package com.dontworry.admin.domain.repository;

import com.dontworry.core.domain.userPlace.entity.UserPlaces;
import com.dontworry.core.domain.userPlace.enums.PlaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPlacesRepository extends JpaRepository<UserPlaces, Long> {

    List<UserPlaces> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<UserPlaces> findByIdAndUserIdAndPlaceStatus(Long id, Long userId, PlaceStatus placeStatus);
}