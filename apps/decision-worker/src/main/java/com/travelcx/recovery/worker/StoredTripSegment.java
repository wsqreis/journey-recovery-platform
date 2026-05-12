package com.travelcx.recovery.worker;

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
