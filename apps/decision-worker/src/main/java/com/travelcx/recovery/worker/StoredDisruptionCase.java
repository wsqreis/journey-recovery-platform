package com.travelcx.recovery.worker;

import java.time.OffsetDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("disruption_case")
public record StoredDisruptionCase(
        @Id String caseId,
        String bookingReference,
        String disruptionType,
        OffsetDateTime detectedAt,
        int impactedPassengers,
        int delayMinutes,
        boolean connectionAtRisk,
        boolean overnightImpact,
        String customerId,
        String customerFullName,
        String loyaltyTier,
        boolean travelingWithChildren,
        boolean requiresAccessibilitySupport,
        String recommendationAction,
        int recommendationScore,
        String recommendationSummary,
        String recommendationExplanation,
        String recommendationReasons,
        OffsetDateTime updatedAt) {}
