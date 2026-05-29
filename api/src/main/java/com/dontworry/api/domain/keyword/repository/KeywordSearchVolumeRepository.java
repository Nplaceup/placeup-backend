package com.dontworry.api.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumes;
import com.dontworry.core.domain.keyword.entity.Keywords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordSearchVolumeRepository extends JpaRepository<KeywordSearchVolumes, Long> {

    KeywordSearchVolumes findByKeyword(Keywords keyword);
    Optional<KeywordSearchVolumes> findTopByKeywordOrderByCreatedAtDesc(Keywords keyword);
}
