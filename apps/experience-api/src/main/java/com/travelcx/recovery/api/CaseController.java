package com.travelcx.recovery.api;

import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.DraftMessageResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final CaseQueryService caseQueryService;

    public CaseController(CaseQueryService caseQueryService) {
        this.caseQueryService = caseQueryService;
    }

    @GetMapping("/{caseId}")
    public DisruptionCaseResponse getCase(@PathVariable String caseId) {
        return caseQueryService.getCase(caseId);
    }

    @GetMapping("/{caseId}/recommendation")
    public RecommendationResponse getRecommendation(@PathVariable String caseId) {
        return caseQueryService.getRecommendation(caseId);
    }

    @GetMapping("/{caseId}/draft-message")
    public DraftMessageResponse previewDraft(@PathVariable String caseId) {
        return caseQueryService.previewDraft(caseId);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleMissingCase(NoSuchElementException exception) {
        return Map.of("message", exception.getMessage());
    }
}
