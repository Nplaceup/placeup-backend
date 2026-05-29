package com.dontworry.api.domain.place.repository;

import com.dontworry.core.domain.place.entity.PlaceReviewAnalysis;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.place.entity.QPlaceReviewAnalysis;
import com.dontworry.core.domain.place.entity.QReviewAnalysis;
import com.dontworry.core.domain.ranking.enums.ReviewAnalysisType;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaceReviewAnalysisRepositoryImpl implements PlaceReviewAnalysisRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaceReviewAnalysis> findLatestByPlaceAndType(Places place, ReviewAnalysisType type) {
        QPlaceReviewAnalysis pra = QPlaceReviewAnalysis.placeReviewAnalysis;
        QPlaceReviewAnalysis sub = new QPlaceReviewAnalysis("sub");
        QReviewAnalysis ra = QReviewAnalysis.reviewAnalysis;

        return queryFactory
                .selectFrom(pra)
                .join(pra.reviewAnalysis, ra).fetchJoin()
                .where(
                        pra.place.eq(place),
                        ra.type.eq(type),
                        pra.crawlDate.eq(
                                JPAExpressions
                                        .select(sub.crawlDate.max())
                                        .from(sub)
                                        .join(sub.reviewAnalysis)
                                        .where(
                                                sub.place.eq(place),
                                                sub.reviewAnalysis.type.eq(type)
                                        )
                        )
                )
                .orderBy(pra.count.desc())
                .fetch();
    }
}