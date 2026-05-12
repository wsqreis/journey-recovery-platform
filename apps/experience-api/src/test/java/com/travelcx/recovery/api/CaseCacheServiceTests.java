package com.travelcx.recovery.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CaseCacheServiceTests {
    private final StoredDisruptionCaseStore disruptionCaseStore = org.mockito.Mockito.mock(StoredDisruptionCaseStore.class);
    private final StoredTripSegmentStore tripSegmentStore = org.mockito.Mockito.mock(StoredTripSegmentStore.class);
    private final CaseCacheService caseCacheService = new CaseCacheService(disruptionCaseStore, tripSegmentStore);

    @Test
    void buildsSnapshotWithRecommendationContext() {
        given(disruptionCaseStore.findById("case-300")).willReturn(Optional.of(new StoredDisruptionCase(
                "case-300",
                "BR3000",
                "FLIGHT_CANCELLATION",
                OffsetDateTime.of(2026, 5, 12, 9, 0, 0, 0, ZoneOffset.UTC),
                3,
                280,
                true,
                true,
                true,
                "cust-300",
                "Alex Kim",
                "CLASSIC",
                true,
                false,
                true,
                false,
                "REBOOK",
                115,
                "Offer an alternative itinerary before customer effort increases.",
                "A missed connection risk makes itinerary recovery time-sensitive.",
                "CRITICAL",
                "15_MINUTES",
                true,
                true,
                "A missed connection risk makes itinerary recovery time-sensitive.||An overnight impact requires accommodation support.",
                OffsetDateTime.now())));
        given(tripSegmentStore.findByCaseId("case-300")).willReturn(List.of(new StoredTripSegment(
                "case-300",
                0,
                "BCN",
                "CDG",
                "VY",
                "VY8240",
                OffsetDateTime.of(2026, 5, 12, 9, 10, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 5, 12, 11, 5, 0, 0, ZoneOffset.UTC))));

        CachedCaseSnapshot snapshot = caseCacheService.getSnapshot("case-300");

        assertThat(snapshot.disruptionCase().highValueItinerary()).isTrue();
        assertThat(snapshot.recommendation().context().priority()).isEqualTo("CRITICAL");
        assertThat(snapshot.recommendation().context().slaBucket()).isEqualTo("15_MINUTES");
        assertThat(snapshot.recommendation().reasons()).hasSize(2);
    }
}
