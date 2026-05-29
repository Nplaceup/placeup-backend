package com.dontworry.api.domain.keyword.repository;

import com.dontworry.core.modeling.entity.Corrs;

import java.util.Optional;

public interface CorrsRepositoryCustom {

    Optional<Corrs> findByKeywordAndEventDate(String keyword);
}
