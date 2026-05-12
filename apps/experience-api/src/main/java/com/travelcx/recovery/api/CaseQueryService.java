package com.travelcx.recovery.api;

import com.travelcx.recovery.contracts.DraftMessageResponse;
import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import org.springframework.stereotype.Service;

@Service
public class CaseQueryService {
    private final CaseCacheService caseCacheService;
    private final DraftMessageService draftMessageService;

    public CaseQueryService(CaseCacheService caseCacheService, DraftMessageService draftMessageService) {
        this.caseCacheService = caseCacheService;
        this.draftMessageService = draftMessageService;
    }

    public DisruptionCaseResponse getCase(String caseId) {
        return caseCacheService.getSnapshot(caseId).disruptionCase();
    }

    public RecommendationResponse getRecommendation(String caseId) {
        return caseCacheService.getSnapshot(caseId).recommendation();
    }

    public DraftMessageResponse previewDraft(String caseId) {
        CachedCaseSnapshot snapshot = caseCacheService.getSnapshot(caseId);
        return new DraftMessageResponse(caseId, draftMessageService.draftMessage(snapshot));
    }
}
