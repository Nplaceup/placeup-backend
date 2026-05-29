package com.dontworry.api.common.exception;

import com.dontworry.api.common.constant.ApiResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ApiResponseCode apiResponseCode;

    public CustomException(ApiResponseCode apiResponseCode) {
        super(apiResponseCode.getMessage());
        this.apiResponseCode = apiResponseCode;
    }

}
