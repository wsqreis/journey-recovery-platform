package com.travelcx.recovery.worker;

import com.travelcx.recovery.contracts.RecommendationContextResponse;
import com.travelcx.recovery.domain.Recommendation;

public final class RecommendationContextMapper {
    private RecommendationContextMapper() {}

    public static RecommendationContextResponse fromRecommendation(Recommendation recommendation) {
        return new RecommendationContextResponse(
                recommendation.context().priority().name(),
                recommendation.context().slaBucket(),
                recommendation.context().humanReviewRequired(),
                recommendation.context().premiumCustomer());
    }
}
