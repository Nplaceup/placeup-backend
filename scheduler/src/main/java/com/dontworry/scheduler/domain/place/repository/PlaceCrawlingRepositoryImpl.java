package com.dontworry.scheduler.domain.place.repository;

import com.dontworry.core.domain.place.entity.PlaceCrawling;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.place.entity.QPlaceCrawling;
import com.dontworry.core.domain.place.entity.QPlaces;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaceCrawlingRepositoryImpl implements PlaceCrawlingRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    @Transactional
    public List<PlaceCrawling> createAndGetThisMonthCrawlingList(LocalDate date) {
        QPlaces p = QPlaces.places;

        // 1. 오늘 크롤링 대상 place 조회
        List<Places> places = queryFactory
                .selectFrom(p)
                .where(notExistsThisMonthHistory(p, date))
                .fetch();

        // 2. crawling_history 생성
        List<PlaceCrawling> crawlingList = places.stream()
                .map(place -> {
                    PlaceCrawling history =
                            PlaceCrawling.buildStarted(place, date);
                    em.persist(history);
                    return history;
                })
                .toList();

        // 3. flush
        em.flush();

        // 4. fetch join으로 재조회
        return crawlingList;
    }

    private BooleanExpression notExistsThisMonthHistory(QPlaces p, LocalDate date) {
        QPlaceCrawling sub = new QPlaceCrawling("sub");

        LocalDate startOfMonth = date.withDayOfMonth(1);

        return JPAExpressions
                .selectOne()
                .from(sub)
                .where(
                        sub.place.eq(p),
                        sub.crawlDate.goe(startOfMonth),
                        sub.crawlDate.lt(startOfMonth.plusMonths(1))
                )
                .notExists();
    }
}
