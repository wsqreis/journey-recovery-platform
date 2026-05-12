package com.travelcx.recovery.api;

import com.travelcx.recovery.contracts.RecommendationContextResponse;

public final class RecommendationContextMapper {
    private RecommendationContextMapper() {}

    public static RecommendationContextResponse fromStoredCase(StoredDisruptionCase storedDisruptionCase) {
        return new RecommendationContextResponse(
                storedDisruptionCase.recommendationPriority(),
                storedDisruptionCase.recommendationSlaBucket(),
                storedDisruptionCase.recommendationHumanReviewRequired(),
                storedDisruptionCase.recommendationPremiumCustomer());
    }
}
