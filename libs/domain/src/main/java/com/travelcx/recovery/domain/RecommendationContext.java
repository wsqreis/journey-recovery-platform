package com.travelcx.recovery.domain;

public record RecommendationContext(
        RecommendationPriority priority,
        String slaBucket,
        boolean humanReviewRequired,
        boolean premiumCustomer) {}
