package com.travelcx.recovery.domain;

import java.time.OffsetDateTime;

public record TripSegment(
        String origin,
        String destination,
        String marketingCarrier,
        String flightNumber,
        OffsetDateTime scheduledDepartureAt,
        OffsetDateTime scheduledArrivalAt) {}
