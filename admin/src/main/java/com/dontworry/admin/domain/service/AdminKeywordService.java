package com.dontworry.admin.domain.service;

import com.dontworry.admin.common.constant.ApiResponseCode;
import com.dontworry.admin.common.exception.CustomException;
import com.dontworry.core.domain.keyword.entity.KeywordIndustry;
import com.dontworry.core.domain.keyword.entity.KeywordLocation;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.admin.domain.repository.KeywordsRepository;
import com.dontworry.admin.domain.repository.KeywordsIndustryRepository;
import com.dontworry.admin.domain.repository.KeywordsLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminKeywordService {

    private final KeywordsRepository keywordsRepository;
    private final KeywordsIndustryRepository keywordsIndustryRepository;
    private final KeywordsLocationRepository keywordsLocationRepository;

    @Transactional
    public KeywordIndustry getOrCreateIndustry(Long industryId, String industryName) {
        if (industryId != null) {
            return keywordsIndustryRepository.findById(industryId)
                    .orElseThrow(() -> new CustomException(ApiResponseCode.INVALID_INDUSTRY));
        }

        if (industryName != null && !industryName.trim().isEmpty()) {
            String normalized = industryName.replaceAll("\\s+", "");
            KeywordIndustry found = keywordsIndustryRepository.findByValue(normalized);
            if (found != null) return found;

            return keywordsIndustryRepository.save(
                    KeywordIndustry.builder()
                            .value(normalized)
                            .priority(5L)
                            .build()
            );
        }

        throw new CustomException(ApiResponseCode.INVALID_INDUSTRY);
    }

    @Transactional
    public KeywordLocation getOrCreateLocation(Long locationId, String locationName) {
        if (locationId != null) {
            return keywordsLocationRepository.findById(locationId)
                    .orElseThrow(() -> new CustomException(ApiResponseCode.INVALID_LOCATION));
        }

        if (locationName != null && !locationName.trim().isEmpty()) {
            String normalized = locationName.replaceAll("\\s+", "");
            KeywordLocation found = keywordsLocationRepository.findByValue(normalized);
            if (found != null) return found;

            return keywordsLocationRepository.save(
                    KeywordLocation.builder()
                            .value(normalized)
                            .priority(5L)
                            .build()
            );
        }

        throw new CustomException(ApiResponseCode.INVALID_LOCATION);
    }

    /**
     * Keywords는 “공백 제거된” 정규화 키워드명으로 저장/업데이트
     * (API 로직 따라 priority=0 우선, priority=1이면 0으로 승격)
     */
    @Transactional
    public Keywords getOrCreateKeyword(String normalizedKeywordName, KeywordIndustry industry, KeywordLocation location) {
        Keywords keywords = keywordsRepository.findByKeywordName(normalizedKeywordName);

        if (keywords != null && keywords.getPriority() == 0L) {
            return keywords;
        }

        if (keywords != null) {
            keywords.setPriorityUserRegistered();
        } else {
            keywords = Keywords.build(normalizedKeywordName, industry, location);
        }

        return keywordsRepository.save(keywords);
    }
}