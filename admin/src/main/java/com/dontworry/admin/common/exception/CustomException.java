package com.dontworry.admin.common.exception;


import com.dontworry.admin.common.constant.ApiResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final ApiResponseCode apiResponseCode;

  public CustomException(ApiResponseCode apiResponseCode) {
    super(apiResponseCode.getMessage());
    this.apiResponseCode = apiResponseCode;
  }

}
