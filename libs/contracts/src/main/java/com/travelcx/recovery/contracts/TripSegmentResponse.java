package com.travelcx.recovery.contracts;

import java.time.OffsetDateTime;

public record TripSegmentResponse(
        String origin,
        String destination,
        String marketingCarrier,
        String flightNumber,
        OffsetDateTime scheduledDepartureAt,
        OffsetDateTime scheduledArrivalAt) {}
