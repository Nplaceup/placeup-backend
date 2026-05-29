package com.dontworry.api.infra.redis.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisResultConsumerRunner implements ApplicationRunner {

    private final AnalysisResultConsumer consumer;

    @Override
    public void run(ApplicationArguments args) {
        consumer.startConsuming(); // 앱 시작 시 자동 실행
    }
}