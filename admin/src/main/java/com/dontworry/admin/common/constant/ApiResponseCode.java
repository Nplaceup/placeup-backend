package com.dontworry.admin.common.constant;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum ApiResponseCode {

    SUCCESS(0, "Success"),
    FAIL(-1, "Failure"),

    REQUEST_ARGUMENT_ERROR(-1000, "Request Argument Error"),

    INVALID_DATE_RANGE(-2000, "잘못된 기간 설정입니다."),
    NEED_PLAN(-2001, "해당 서비스 이용을 위해서는 이용권 결제가 필요합니다."),

    EXPIRED_TOKEN(-3000, "Expired or Modulated Token"),
    MISSING_TOKEN(-3001, "Missing Token"),
    INVALID_USER(-3002, "Invalid User"),
    NOT_EXIST_FILE(-3003, "파일이 존재하지 않습니다."),
    FAIL_UPLOAD_IMAGE(-3004, "이미지 업로드에 실패했습니다."),
    NOT_EXIST_FILE_EXTENSION(-3005, "확장자가 존재하지 않습니다."),
    INVALID_FILE_EXTENSION(-3006, "허용하지 않는 확장자입니다."),
    UNLINK_FAILED(-3007, "회원 탈퇴에 실패했습니다."),

    INVALID_URL(-4000, "잘못된 링크입니다. 다시 확인해주세요."),
    PLACE_NOT_FOUND(-4001, "등록되어 있는 사용자의 플레이스가 아닙니다. 플레이스 아이디를 다시 확인해주세요."),
    ALREADY_HAVE_PLACE(-4002, "이미 플레이스를 등록한 회원입니다. 등록이 아닌 수정이 필요합니다."),
    ACTIVE_PLACE_NOT_FOUND(-4003, "등록된 플레이스가 없습니다."),
    INVALID_PLACE(-4004, "사용자의 플레이스가 아닙니다."),
    PLACE_DUPLICATE(-4005, "등록되어 있는 플레이스입니다. URL을 다시 확인하세요."),

    INVALID_AMOUNT(-5000, "금액 정보가 유효하지 않습니다."),
    PAYMENT_SESSION_EXPIRED(-5001, "결제 시간이 만료되었습니다."),
    CARD_REJECTED(-5002, "카드사 승인 거절"),
    FORBIDDEN_PAYMENT_REQUEST(-5003, "잘못된 결제 요청입니다."),
    INVALID_API_KEY(-5004, "잘못된 시크릿 키입니다."),
    PAYMENT_FAILED(-5005, "결제 실패"),
    EXTERNAL_API_ERROR(-5006, "외부 결제 API 통신 중 오류 발생"),
    INVALID_ID(-5007, "결제 정보를 찾을 수 없습니다."),
    NOT_ENOUGH_CASH(-5008, "캐시가 충분하지 않습니다."),
    UNSUPPORTED_USAGE_TYPE(-5009, "지원하지 않는 판매 상품입니다."),
    INVALID_ITEMS(-5010, "최소 1개 이상의 항목은 선택하셔야 합니다."),

    SOLUTION_NOT_FOUND(-6000, "솔루션 내역이 확인되지 않습니다."),
    INVALID_SOLUTION(-6001, "회원님께서 신청하신 솔루션 내역이 아닙니다."),
    CAN_NOT_CANCEL_SOLUTION(-6002, "집행중이거나 완료된 솔루션은 취소할 수 없습니다."),
    LAST_SOLUTION_IN_PROGRESS(-6003, " 이미 집행중이거나 신청한 솔루션 내역이 있습니다."),

    KEYWORD_LIMIT_EXCEEDED(-7000, "키워드가 너무 많습니다. 삭제 후 다시 추가하세요."),
    DUPLICATE_KEYWORD(-7001, "이미 등록한 키워드입니다."),
    INVALID_KEYWORD(-7002, "없는 키워드입니다."),
    NOT_OWN_KEYWORD(-7003, "회원님이 등록한 키워드가 아닙니다."),
    CAN_NOT_DELETE_KEYWORD(-7004, "집행중인 솔루션이 있는 키워드는 삭제가 불가합니다."),
    INVALID_INDUSTRY(-7005, "유효하지 않은 업종입니다."),
    INVALID_LOCATION(-7006, "유효하지 않은 지역입니다."),
    INVALID_INDUSTRY_INPUT(-7007, "업종 ID와 업종명 중 하나만 입력해주세요."),
    INVALID_LOCATION_INPUT(-7008, "지역 ID와 지역명 중 하나만 입력해주세요."),
    INVALID_KEYWORD_PRIORITY(-7009, "유효하지 않은 키워드 우선순위입니다."),

    TOO_MANY_ACCOUNT(-8000, "이미 계정이 등록되어 있습니다. 기존 계정을 수정해주세요."),
    INVALID_ACCOUNT(-8001, "계정의 아이디 및 암호나 2차 인증이 해지되었는지 확인해주세요."),
    NOT_OWN_ACCOUNT(-8002, "회원님의 아이디가 아닙니다."),
    UNSUPPORTED_SNS_TYPE(-8003, "지원하지 않는 플랫폼입니다."),
    SNS_NOT_FOUND(-8004, "등록된 계정이 확인되지 않습니다."),
    DUPLICATE_ACCOUNT(8005, "이미 등록된 계정입니다."),
    ACCOUNT_DELETE_NOT_ALLOWED(-8006, "서로 리뷰 신청건이 많아 계정 삭제가 어렵습니다."),
    ACCOUNT_LIMIT_EXCEED(-8007, "서로 리뷰 계정은 10개 이상 등록이 불가합니다."),
    NEED_ACCOUNT(-8008, "SNSType에 맞는 계정 등록이 필요합니다."),
    ALREADY_DELETED_ACCOUNT(-8009, "이미 삭제된 아이디입니다."),

    DUPLICATE_PRODUCT(-9000, "이미 등록한 상품입니다."),
    NOT_OWN_PRODUCT(-9001, "수정 가능한 상품이 확인되지 않습니다."),
    PRODUCT_NOT_FOUND(-9002, "상품이 확인되지 않습니다."),

    TOO_MANY_EXCHANGE_REQUEST(-10000, "남은 신청 가능한 발행 건수보다 많습니다."),
    DUPLICATE_PRODUCT_EXCHANGE(-10001, "이미 신청한 상품입니다."),
    INVALID_REVIEW_EXCHANGE(-10002, "서로 리뷰 내역이 확인되지 않습니다."),
    INACTIVE_REVIEW_EXCHANGE(-10003, "이미 취소된 서로 리뷰 내역입니다."),
    NOT_OWN_REVIEW_EXCHANGE(-10004, "회원님이 신청하신 서로 리뷰 내역이 아닙니다."),

    UNEXPECTED_WRITE_TYPE(-11000, "적절하지 않은 WriteType입니다."),
    DAILY_LIMIT_EXCEED(-11001, "하루에 딸깍 글쓰기는 2회만 작성이 가능합니다."),
    SNS_POST_NOT_FOUND(-11002, "딸깍 글쓰기 정보가 확인되지 않습니다."),
    INVALID_STATUS_CHANGE(-11003, "해당 상태로 변경이 불가합니다."),
    NOT_LATEST_SNS_POST(-11004, "작성중인 최근 딸각 글쓰기 건이 아닙니다."),
    UNAUTHORIZED_ACCESS(-11005, "접근 권한이 없습니다."),
    CANNOT_RETRY_SUCCESS_POST(-11006, "성공한 포스팅은 재시도할 수 없습니다."),
    MAX_RETRY_EXCEEDED(-11007, "최대 재시도 횟수를 초과했습니다."),
    NO_FAILED_POSTS_TO_RETRY(-11008, "재시도할 실패한 포스트가 없습니다."),
    ;

    private final Integer code;
    private final String message;

    public static ApiResponseCode getApiResponseCode(final Integer code) {
        for (ApiResponseCode apiCode : ApiResponseCode.values()) {
            if (Objects.equals(apiCode.code, code)) {
                return apiCode;
            }
        }
        return ApiResponseCode.FAIL;
    }

    public static ApiResponseCode getApiResponseCode(final String message) {
        for (ApiResponseCode apiCode : ApiResponseCode.values()) {
            if (apiCode.message.equals(message)) {
                return apiCode;
            }
        }
        return ApiResponseCode.FAIL;
    }
}