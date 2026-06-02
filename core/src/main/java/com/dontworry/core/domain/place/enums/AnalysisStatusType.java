package com.dontworry.core.domain.place.enums;

public enum AnalysisStatusType {

    REQUESTED,              // 분석 요청 등록
    PLACE_CRAWLING,         // 매장 정보 수집 중
    REVIEW_CRAWLING,        // 리뷰 데이터 수집 중
    KEYWORD_EXTRACTING,     // 키워드 추출 중 (Python round=1)
    RANKING_CRAWLING,       // 키워드별 순위 수집 중
    SEARCH_VOLUME_CRAWLING, // 키워드 검색량 수집 중
    SEO_ANALYZING,          // SEO 점수 계산 중 (Python round=2)
    COMPLETED,              // 분석 완료
    FAILED                  // 분석 실패
}
