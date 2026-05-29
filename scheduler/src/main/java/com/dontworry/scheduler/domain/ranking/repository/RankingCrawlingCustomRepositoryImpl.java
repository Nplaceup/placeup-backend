package com.dontworry.scheduler.domain.ranking.repository;

import com.dontworry.core.domain.keyword.entity.*;
import com.dontworry.core.domain.ranking.entity.QRankingCrawling;
import com.dontworry.core.domain.ranking.entity.RankingCrawling;
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
public class RankingCrawlingCustomRepositoryImpl implements RankingCrawlingCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    @Transactional
    public List<RankingCrawling> createAndGetTodayCrawlingList(LocalDate date, List<Long> priorities) {
        QKeywords k = QKeywords.keywords;

        // 오늘 크롤링 대상 keyword 조회
        List<Keywords> keywords = queryFactory
                .selectFrom(k)
                .where(
                        k.priority.in(priorities),
                        notExistsTodayHistory(k, date)
                )
                .fetch();

        // crawling_history 생성
        List<RankingCrawling> crawlingList = keywords.stream()
                .map(keyword -> {
                    RankingCrawling history =
                            RankingCrawling.buildStarted(keyword, date);
                    em.persist(history);
                    return history;
                })
                .toList();

        // flush (insert 반영)
        em.flush();

        // fetch join으로 다시 조회
        return crawlingList;
    }

    private BooleanExpression notExistsTodayHistory(QKeywords k, LocalDate date) {
        QRankingCrawling sub = new QRankingCrawling("sub");

        return JPAExpressions
                .selectOne()
                .from(sub)
                .where(
                        sub.keyword.eq(k),
                        sub.crawlDate.eq(date)
                )
                .notExists();
    }

}
