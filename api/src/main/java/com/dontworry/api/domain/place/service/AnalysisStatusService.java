package com.dontworry.api.domain.place.service;

import com.dontworry.api.domain.place.repository.AnalysisStatusRepository;
import com.dontworry.core.domain.place.entity.AnalysisStatus;
import com.dontworry.core.domain.place.enums.AnalysisStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisStatusService {

    private final AnalysisStatusRepository analysisStatusRepository;

    @Transactional
    public void update(Long placeId, AnalysisStatusType status) {
        AnalysisStatus analysisStatus = analysisStatusRepository
                .findByPlaceId(placeId)
                .orElse(AnalysisStatus.of(placeId, status));

        analysisStatus.updateStatus(status);
        analysisStatusRepository.save(analysisStatus);

        log.info("[AnalysisStatus] placeId={}, status={}", placeId, status);
    }

    public AnalysisStatusType getStatus(Long placeId) {
        return analysisStatusRepository
                .findByPlaceId(placeId)
                .map(AnalysisStatus::getStatus)
                .orElse(null);
    }
}
