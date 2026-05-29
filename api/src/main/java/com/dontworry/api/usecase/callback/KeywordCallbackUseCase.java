package com.dontworry.api.usecase.callback;

import com.dontworry.api.controller.callback.dto.KeywordCallbackRequest;
import com.dontworry.api.domain.keyword.service.KeywordSearchVolumeCrawlingService;
import com.dontworry.api.domain.keyword.service.KeywordSearchVolumeService;
import com.dontworry.api.domain.keyword.service.KeywordService;
import com.dontworry.core.domain.keyword.entity.KeywordRelated;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.keyword.enums.CrawlingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordCallbackUseCase {

    private final KeywordService keywordService;
    private final KeywordSearchVolumeCrawlingService keywordSearchVolumeCrawlingService;
    private final KeywordSearchVolumeService keywordSearchVolumeService;

    @Transactional
    public void processCallback(KeywordCallbackRequest request) {
        Keywords keyword = keywordService.getKeyword(request.keyword());

        // 크롤링 결과 없음 → FAILED 처리
        if (request.data() == null
                || request.data().keywordList() == null
                || request.data().keywordList().isEmpty()) {
            keywordSearchVolumeCrawlingService.updateCrawlingStatus(keyword, request, CrawlingStatus.FAILED);
            log.warn("keyword callback 결과 없음 [{}]", request.keyword());
            return;
        }

        List<KeywordCallbackRequest.RelKwdStat> keywordList = request.data().keywordList();

        // 1. 입력 키워드 본인의 검색량 저장
        KeywordCallbackRequest.RelKwdStat mainStat = keywordList.stream()
                .filter(s -> s.relKeyword().equals(request.keyword()))
                .findFirst()
                .orElse(keywordList.getFirst());
        keywordSearchVolumeService.saveKeywordSearchVolume(keyword, mainStat);
        log.info("검색량 저장 완료 [{}] volume={}", request.keyword(), mainStat.totalSearchVolume());

        // 2. 연관 키워드 전체 저장
        List<KeywordRelated> relatedList = keywordList.stream()
                .map(stat -> KeywordRelated.builder()
                        .keyword(keyword)
                        .name(stat.relKeyword())
                        .monthlySearchVolume(stat.totalSearchVolume())
                        .competitionLevel(stat.compIdx())
                        .build())
                .toList();
        keywordService.saveAllKeywordRelated(request, relatedList);

        // 3. 크롤링 상태 → COMPLETED
        keywordSearchVolumeCrawlingService.updateCrawlingStatus(keyword, request, CrawlingStatus.COMPLETED);
    }
}
