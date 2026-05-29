package com.dontworry.scheduler.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordSearchVolumeRepository extends JpaRepository<KeywordSearchVolumes, Long> {
}
