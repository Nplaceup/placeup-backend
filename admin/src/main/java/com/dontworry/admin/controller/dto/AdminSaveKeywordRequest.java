package com.dontworry.admin.controller.dto;

import com.dontworry.admin.common.constant.ApiResponseCode;
import com.dontworry.admin.common.exception.CustomException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminSaveKeywordRequest {

    @NotNull(message = "userPlaceId는 필수입니다.")
    private Long userPlaceId;

    private Long industryId;

    @Pattern(regexp = "^$|^[가-힣a-zA-Z0-9 ]+$", message = "업종명은 한글, 영문, 숫자, 공백만 입력할 수 있습니다.")
    private String industryName;

    private Long locationId;

    @Pattern(regexp = "^$|^[가-힣a-zA-Z0-9 ]+$", message = "지역명은 한글, 영문, 숫자, 공백만 입력할 수 있습니다.")
    private String locationName;

    public void validate() {
        boolean hasIndustryId = industryId != null;
        boolean hasIndustryName = industryName != null && !industryName.trim().isEmpty();

        boolean hasLocationId = locationId != null;
        boolean hasLocationName = locationName != null && !locationName.trim().isEmpty();

        if (!hasIndustryId && !hasIndustryName) throw new CustomException(ApiResponseCode.INVALID_INDUSTRY);
        if (!hasLocationId && !hasLocationName) throw new CustomException(ApiResponseCode.INVALID_LOCATION);

        // “둘 중 하나만” 정책이면 XOR까지 강제 (API 주석과 동일)
        if (hasIndustryId && hasIndustryName) throw new CustomException(ApiResponseCode.INVALID_INDUSTRY);
        if (hasLocationId && hasLocationName) throw new CustomException(ApiResponseCode.INVALID_LOCATION);
    }
}