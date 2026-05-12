package com.travelcx.recovery.api;

import java.time.OffsetDateTime;

public record StoredTripSegment(
        String caseId,
        int segmentOrder,
        String origin,
        String destination,
        String marketingCarrier,
        String flightNumber,
        OffsetDateTime scheduledDepartureAt,
        OffsetDateTime scheduledArrivalAt) {}
