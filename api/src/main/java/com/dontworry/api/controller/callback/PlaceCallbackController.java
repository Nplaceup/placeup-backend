package com.dontworry.api.controller.callback;

import com.dontworry.api.common.constant.Uri;
import com.dontworry.api.controller.callback.dto.PlaceCallbackResponse;
import com.dontworry.api.usecase.callback.PlaceCallbackUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(Uri.PLACE_CALLBACK)
@RequiredArgsConstructor
public class PlaceCallbackController {

    private final PlaceCallbackUseCase placeCallbackUseCase;

    @PostMapping
    public void callback(
            @RequestBody(required = false) PlaceCallbackResponse callbackResponse
    ) {
        log.info("[Places Crawler Callback] Process Place Callback start");
        placeCallbackUseCase.processPlacesCallback(callbackResponse);
        log.info("[Places Crawler Callback] Process Place Callback end");
    }

}
