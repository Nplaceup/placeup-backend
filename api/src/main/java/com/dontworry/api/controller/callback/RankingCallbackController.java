package com.dontworry.api.controller.callback;

import com.dontworry.api.common.constant.Uri;
import com.dontworry.api.controller.callback.dto.RankingCallbackResponse;
import com.dontworry.api.usecase.callback.RankingCallbackUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(Uri.RANKING_CALLBACK)
@RequiredArgsConstructor
public class RankingCallbackController {

    private final RankingCallbackUseCase rankingCallbackUseCase;

    @PostMapping
    public void callback(
            @RequestBody(required = false) RankingCallbackResponse callbackResponse
    ) {
        log.info("[Rankings Crawler Callback] Process Callback start");
        if (callbackResponse == null) {
            rankingCallbackUseCase.processRankingsCallback(null, null, null);
        } else {
            rankingCallbackUseCase.processRankingsCallback(
                    callbackResponse.keyword(), callbackResponse.crawlDate(), callbackResponse.crawlerResponses());
        }
        log.info("[Rankings Crawler Callback] Process Callback end");
    }

}
