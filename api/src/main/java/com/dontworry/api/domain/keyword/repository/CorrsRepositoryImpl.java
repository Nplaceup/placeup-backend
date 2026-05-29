package com.dontworry.api.domain.keyword.repository;

import com.dontworry.core.modeling.entity.Corrs;
import com.dontworry.core.modeling.entity.QCorrs;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CorrsRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public Corrs findByKeywordAndEventDate(String keyword) {

        QCorrs corrs = QCorrs.corrs;

        return queryFactory
                .selectFrom(corrs)
                .orderBy(corrs.eventDate.desc())
                .orderBy(corrs.id.desc())
                .fetchFirst();
    }
}
