package com.dontworry.core.domain.keyword.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Level {
    HIGH("높음"),
    MIDDLE("보통"),
    LOW("낮음");

    private final String nameKor;

    public static Level fromKor(String kor) {
        return Arrays.stream(values())
                .filter(level -> level.nameKor.equals(kor))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid competition level (kor): " + kor)
                );
    }
}
