package com.dontworry.api.domain.keyword.repository;

import com.dontworry.core.modeling.entity.Corrs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrsRepository extends JpaRepository<Corrs, Long>, CorrsRepositoryCustom {
}
