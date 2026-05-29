package com.dontworry.core.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    EASY_PAY("간편결제"),
    MOBILE("휴대폰"),
    TRANSFER("계좌이체"),
    ADMIN_PROVIDED ("관리자지급")
    ;

    private final String koreanName;

    public static PaymentMethod fromKorean(String koreanName) {
        return Arrays.stream(values())
                .filter(pm -> pm.koreanName.equals(koreanName))
                .findFirst()
                .orElse(null);
    }
}
