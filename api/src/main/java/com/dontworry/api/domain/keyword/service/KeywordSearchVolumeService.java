package com.dontworry.api.domain.keyword.service;

import com.dontworry.api.controller.callback.dto.KeywordCallbackRequest;
import com.dontworry.api.domain.keyword.repository.KeywordSearchVolumeRepository;
import com.dontworry.core.domain.keyword.entity.KeywordSearchVolumes;
import com.dontworry.core.domain.keyword.entity.Keywords;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeywordSearchVolumeService {

    private final KeywordSearchVolumeRepository keywordSearchVolumeRepository;

    @Transactional
    public void saveKeywordSearchVolume(Keywords keyword, KeywordCallbackRequest.RelKwdStat mainStat) {
        KeywordSearchVolumes searchVolume = KeywordSearchVolumes.build(
                keyword,
                mainStat.totalSearchVolume(),
                mainStat.compIdx()
        );
        keywordSearchVolumeRepository.save(searchVolume);
    }

}
