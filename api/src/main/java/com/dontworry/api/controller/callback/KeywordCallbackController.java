package com.dontworry.api.controller.callback;

import com.dontworry.api.common.constant.Uri;
import com.dontworry.api.controller.callback.dto.KeywordCallbackRequest;
import com.dontworry.api.usecase.callback.KeywordCallbackUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(Uri.KEYWORD_CALLBACK)
@RequiredArgsConstructor
public class KeywordCallbackController {

    private final KeywordCallbackUseCase keywordCallbackUseCase;

    @PostMapping
    public void callback(
            @RequestBody KeywordCallbackRequest request
    ) {
        log.info("keyword callback start [{}]", request.keyword());
        keywordCallbackUseCase.processCallback(request);
        log.info("keyword callback end [{}]", request.keyword());
    }
}