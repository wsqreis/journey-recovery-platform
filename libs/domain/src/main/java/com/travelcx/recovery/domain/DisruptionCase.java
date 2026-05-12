package com.travelcx.recovery.domain;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public record DisruptionCase(
        String caseId,
        String bookingReference,
        DisruptionType disruptionType,
        OffsetDateTime detectedAt,
        int impactedPassengers,
        int delayMinutes,
        boolean connectionAtRisk,
        boolean overnightImpact,
        boolean highValueItinerary,
        CustomerProfile customerProfile,
        List<TripSegment> impactedSegments) {
    public Duration disruptionDuration() {
        return Duration.ofMinutes(delayMinutes);
    }
}
