package com.dontworry.api.domain.keyword.service;

import com.dontworry.api.controller.callback.dto.KeywordCallbackRequest;
import com.dontworry.core.domain.keyword.entity.*;
import com.dontworry.api.domain.keyword.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordsRepository keywordsRepository;
    private final KeywordRelatedRepository keywordRelatedRepository;

    @Transactional
    public void saveAllKeywordRelated(KeywordCallbackRequest request, List<KeywordRelated> relatedList) {
        keywordRelatedRepository.saveAll(relatedList);
        log.info("연관 키워드 저장 완료, keyword={}, relatedKeywords={}", request.keyword(), relatedList);
    }

    public Keywords getKeyword(String keyword) {
        return keywordsRepository.findByKeywordName(keyword.replaceAll("\\s+", ""));
    }

}