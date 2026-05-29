package com.dontworry.api.common.exception;

import com.dontworry.api.common.dto.ApiResponse;
import com.dontworry.api.common.constant.ApiResponseCode;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 파라미터 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ApiResponseCode.REQUEST_ARGUMENT_ERROR, errors));
    }

    // 토큰 없음 예외 처리
    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<?> MissingRequestCookieException(MissingRequestCookieException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(ApiResponseCode.MISSING_TOKEN, e.getMessage()));
    }

    // 토큰 만료 예외 처리
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(ApiResponseCode.EXPIRED_TOKEN));
    }

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomExceptions(CustomException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(e.getApiResponseCode()));
    }

    // 위에서 처리되지 않은 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleExceptions(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ApiResponseCode.FAIL));
    }

}
