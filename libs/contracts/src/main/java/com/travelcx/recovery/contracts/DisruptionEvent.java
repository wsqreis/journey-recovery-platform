package com.travelcx.recovery.contracts;

import java.time.OffsetDateTime;
import java.util.List;

public record DisruptionEvent(
        String eventId,
        String caseId,
        String bookingReference,
        String disruptionType,
        OffsetDateTime detectedAt,
        int impactedPassengers,
        int delayMinutes,
        boolean connectionAtRisk,
        boolean overnightImpact,
        CustomerProfileResponse customer,
        List<TripSegmentResponse> impactedSegments) {}
