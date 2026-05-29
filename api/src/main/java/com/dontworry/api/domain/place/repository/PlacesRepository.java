package com.dontworry.api.domain.place.repository;

import com.dontworry.core.domain.place.entity.Places;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlacesRepository extends JpaRepository<Places, Long> {

    Places findByNaverPlaceId(Long naverPlaceId);

}
