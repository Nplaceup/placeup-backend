package com.dontworry.api.controller.analysis;

import com.dontworry.api.common.constant.Uri;
import com.dontworry.api.common.dto.ApiResponse;
import com.dontworry.api.controller.analysis.dto.AnalysisStatusResponse;
import com.dontworry.api.controller.analysis.dto.PlaceAnalysisResponse;
import com.dontworry.api.usecase.analysis.AnalysisResultUseCase;
import com.dontworry.api.usecase.analysis.PlaceAnalysisUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Uri.PLACE_ANALYSIS)
@RequiredArgsConstructor
public class PlaceAnalysisController {

    private final PlaceAnalysisUseCase placeAnalysisUseCase;
    private final AnalysisResultUseCase analysisResultUseCase;

    @PostMapping
    public ResponseEntity<?> startAnalysis(@RequestBody String url) {
        PlaceAnalysisResponse result = placeAnalysisUseCase.getOrRequestAnalysis(url);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<?> getAnalysisResult(@RequestParam Long naverPlaceId) {
        AnalysisStatusResponse result = analysisResultUseCase.getResult(naverPlaceId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
