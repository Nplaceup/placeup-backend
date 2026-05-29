package com.dontworry.admin.domain.service;

import com.dontworry.admin.common.constant.ApiResponseCode;
import com.dontworry.admin.common.exception.CustomException;
import com.dontworry.admin.controller.dto.AdminSaveKeywordRequest;
import com.dontworry.admin.domain.repository.PlaceKeywordsRepository;
import com.dontworry.admin.domain.repository.UserPlacesRepository;
import com.dontworry.core.domain.keyword.entity.KeywordIndustry;
import com.dontworry.core.domain.keyword.entity.KeywordLocation;
import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.userPlaceKeyword.entity.UserPlaceKeywords;

import com.dontworry.core.domain.userPlaceKeyword.enums.PlaceKeywordStatus;
import com.dontworry.core.domain.userPlace.entity.UserPlaces;
import com.dontworry.core.domain.userPlace.enums.PlaceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPlaceKeywordsManageService {

    private final UserPlacesRepository userPlacesRepository;
    private final PlaceKeywordsRepository placeKeywordsRepository;
    private final AdminKeywordService adminKeywordService;

    /**
     * API PlaceKeywordsService.savePlaceKeyword() 흐름 그대로
     */
    @Transactional
    public void addKeywordToUserPlace(Long targetUserId, AdminSaveKeywordRequest request) {

        // 1) 요청 검증
        request.validate();

        // userPlace 소유/ACTIVE 검증
        UserPlaces userPlace = userPlacesRepository
                .findByIdAndUserIdAndPlaceStatus(request.getUserPlaceId(), targetUserId, PlaceStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ApiResponseCode.PLACE_NOT_FOUND));

        // 2) UserPlaceKeywords 검증
        // 2-1) 키워드 개수 제한 체크 (6개)
        List<UserPlaceKeywords> currentKeywords = placeKeywordsRepository
                .findAllByUserPlacesAndStatusOrderByCreatedAtDesc(userPlace, PlaceKeywordStatus.ACTIVE);

        if (currentKeywords.size() >= 6) {
            throw new CustomException(ApiResponseCode.KEYWORD_LIMIT_EXCEEDED);
        }

        // 2-2) Industry/Location 조회 또는 생성
        KeywordIndustry industry = adminKeywordService.getOrCreateIndustry(
                request.getIndustryId(), request.getIndustryName()
        );

        KeywordLocation location = adminKeywordService.getOrCreateLocation(
                request.getLocationId(), request.getLocationName()
        );

        // 2-3) UserPlaceKeywords “원본(공백 포함)” 키워드 생성
        String originalKeywordName = location.getValue() + " " + industry.getValue();

        // 2-4) 중복 체크(원본 기준) — (더 엄격히 하려면 normalize 비교로 바꿔도 됨)
        boolean exists = currentKeywords.stream()
                .filter(pk -> pk.getStatus() == PlaceKeywordStatus.ACTIVE)
                .anyMatch(pk -> pk.getKeywordName().equals(originalKeywordName));

        if (exists) {
            throw new CustomException(ApiResponseCode.DUPLICATE_KEYWORD);
        }

        // 3) 저장
        // 3-1) Keywords 테이블에 “공백 제거” 정규화 키워드 저장/업데이트
        String normalizedKeywordName = originalKeywordName.replaceAll("\\s+", "");
        Keywords keyword = adminKeywordService.getOrCreateKeyword(normalizedKeywordName, industry, location);

        // 3-2) UserPlaceKeywords (원본) 생성/저장
        UserPlaceKeywords placeKeyword;
        if (currentKeywords.isEmpty()) {
            placeKeyword = UserPlaceKeywords.buildInitial(userPlace, keyword, originalKeywordName);
        } else {
            placeKeyword = UserPlaceKeywords.build(userPlace, keyword, originalKeywordName);
        }
        placeKeywordsRepository.save(placeKeyword);
    }
}