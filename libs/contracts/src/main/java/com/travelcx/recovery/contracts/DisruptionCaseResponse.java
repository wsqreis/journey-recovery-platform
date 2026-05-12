package com.travelcx.recovery.contracts;

import java.time.OffsetDateTime;
import java.util.List;

public record DisruptionCaseResponse(
        String caseId,
        String bookingReference,
        String disruptionType,
        OffsetDateTime detectedAt,
        int impactedPassengers,
        int delayMinutes,
        boolean connectionAtRisk,
        boolean overnightImpact,
        boolean highValueItinerary,
        CustomerProfileResponse customer,
        List<TripSegmentResponse> impactedSegments) {}
