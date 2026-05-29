package com.dontworry.scheduler.domain.keyword.repository;

import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumeCrawling;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.entity.QKeywordSearchVolumeCrawling;
import com.dontworry.core.domain.keyword.entity.QKeywords;
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
public class KeywordSearchVolumeCrawlingRepositoryImpl implements KeywordSearchVolumeCrawlingRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    @Transactional
    public List<KeywordSearchVolumeCrawling> createAndGetThisMonthCrawlingList(LocalDate date) {
        QKeywords k = QKeywords.keywords;

        // 1. 이번달 크롤링 대상 keyword 조회
        List<Keywords> keywords = queryFactory
                .selectFrom(k)
                .where(notExistsThisMonthHistory(k, date))
                .fetch();

        // 2. crawling 생성
        List<KeywordSearchVolumeCrawling> crawlingList = keywords.stream()
                .map(keyword -> {
                    KeywordSearchVolumeCrawling crawling =
                            KeywordSearchVolumeCrawling.buildStarted(keyword, date);
                    em.persist(crawling);
                    return crawling;
                })
                .toList();

        // 3. flush & clear
        em.flush();

        // 4. fetch join으로 재조회
        return crawlingList;
    }


    private BooleanExpression notExistsThisMonthHistory(QKeywords k, LocalDate date) {
        QKeywordSearchVolumeCrawling sub = new QKeywordSearchVolumeCrawling("sub");

        LocalDate startOfMonth = date.withDayOfMonth(1);

        return JPAExpressions
                .selectOne()
                .from(sub)
                .where(
                        sub.keyword.eq(k),
                        sub.crawlDate.goe(startOfMonth),
                        sub.crawlDate.lt(startOfMonth.plusMonths(1))
                )
                .notExists();
    }
}
