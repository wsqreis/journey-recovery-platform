package com.travelcx.recovery.worker;

import java.time.OffsetDateTime;

public record StoredDisruptionCase(
        String caseId,
        String bookingReference,
        String disruptionType,
        OffsetDateTime detectedAt,
        int impactedPassengers,
        int delayMinutes,
        boolean connectionAtRisk,
        boolean overnightImpact,
        boolean highValueItinerary,
        String customerId,
        String customerFullName,
        String loyaltyTier,
        boolean travelingWithChildren,
        boolean requiresAccessibilitySupport,
        boolean vipCustomer,
        boolean corporateTraveler,
        String recommendationAction,
        int recommendationScore,
        String recommendationSummary,
        String recommendationExplanation,
        String recommendationPriority,
        String recommendationSlaBucket,
        boolean recommendationHumanReviewRequired,
        boolean recommendationPremiumCustomer,
        String recommendationReasons,
        OffsetDateTime updatedAt) {}
