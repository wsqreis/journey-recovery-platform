package com.travelcx.recovery.contracts;

public record RecommendationContextResponse(
        String priority,
        String slaBucket,
        boolean humanReviewRequired,
        boolean premiumCustomer) {}
