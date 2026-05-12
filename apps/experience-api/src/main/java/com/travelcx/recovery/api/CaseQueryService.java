package com.travelcx.recovery.api;

import com.travelcx.recovery.contracts.DraftMessageResponse;
import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import org.springframework.stereotype.Service;

@Service
public class CaseQueryService {
    private final CaseCacheService caseCacheService;

    public CaseQueryService(CaseCacheService caseCacheService) {
        this.caseCacheService = caseCacheService;
    }

    public DisruptionCaseResponse getCase(String caseId) {
        return caseCacheService.getSnapshot(caseId).disruptionCase();
    }

    public RecommendationResponse getRecommendation(String caseId) {
        return caseCacheService.getSnapshot(caseId).recommendation();
    }

    public DraftMessageResponse previewDraft(String caseId) {
        CachedCaseSnapshot snapshot = caseCacheService.getSnapshot(caseId);
        DisruptionCaseResponse disruptionCase = snapshot.disruptionCase();
        RecommendationResponse recommendation = snapshot.recommendation();
        String draftMessage = "Hello %s, we detected a %s on booking %s. Our current recommendation is to %s. %s"
                .formatted(
                        disruptionCase.customer().fullName(),
                        disruptionCase.disruptionType().toLowerCase().replace('_', ' '),
                        disruptionCase.bookingReference(),
                        recommendation.action().toLowerCase().replace('_', ' '),
                        recommendation.summary());
        return new DraftMessageResponse(caseId, draftMessage);
    }
}
