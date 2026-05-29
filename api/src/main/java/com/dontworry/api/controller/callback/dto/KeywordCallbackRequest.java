package com.dontworry.api.controller.callback.dto;

import java.time.LocalDate;
import java.util.List;

public record KeywordCallbackRequest(
        String keyword,
        LocalDate crawlDate,
        KeywordData data
) {
    public record KeywordData(
            List<RelKwdStat> keywordList
    ) {}

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
            try {
                return Integer.parseInt(v.trim());
            } catch (Exception e) {
                return 0;
            }
        }
    }
}