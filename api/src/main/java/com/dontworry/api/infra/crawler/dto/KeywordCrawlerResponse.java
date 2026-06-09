package com.dontworry.api.infra.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

public record KeywordCrawlerResponse(
        List<RelKwdStat> keywordList
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RelKwdStat(
            String relKeyword,
            String monthlyPcQcCnt,
            String monthlyMobileQcCnt,
            String compIdx
    ) {
        public int totalSearchVolume() {
            return safeInt(monthlyPcQcCnt) + safeInt(monthlyMobileQcCnt);
        }

        private int safeInt(String v) {
            if (v == null) return 0;
            try { return Integer.parseInt(v.trim()); }
            catch (Exception e) { return 0; }
        }
    }
}
