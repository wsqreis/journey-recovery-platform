package com.travelcx.recovery.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CaseQueryPersistenceIntegrationTests {
    @Autowired
    private StoredDisruptionCaseStore disruptionCaseStore;

    @Autowired
    private StoredTripSegmentStore tripSegmentStore;

    @Autowired
    private CaseQueryService caseQueryService;

    @BeforeEach
    void setUp() {
        disruptionCaseStore.deleteAll();
        disruptionCaseStore.upsert(new StoredDisruptionCase(
                "case-300",
                "BR3000",
                "FLIGHT_CANCELLATION",
                OffsetDateTime.of(2026, 5, 12, 9, 0, 0, 0, ZoneOffset.UTC),
                3,
                280,
                true,
                true,
                "cust-300",
                "Alex Kim",
                "CLASSIC",
                true,
                false,
                "REBOOK",
                115,
                "Offer an alternative itinerary before customer effort increases.",
                "A missed connection risk makes itinerary recovery time-sensitive.",
                "A missed connection risk makes itinerary recovery time-sensitive.||An overnight impact requires accommodation support.",
                OffsetDateTime.now()));
        tripSegmentStore.replaceSegments(
                "case-300",
                List.of(new StoredTripSegment(
                        "case-300",
                        0,
                        "BCN",
                        "CDG",
                        "VY",
                        "VY8240",
                        OffsetDateTime.of(2026, 5, 12, 9, 10, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 12, 11, 5, 0, 0, ZoneOffset.UTC))));
    }

    @Test
    void readsPersistedCaseAndRecommendation() {
        var disruptionCase = caseQueryService.getCase("case-300");
        var recommendation = caseQueryService.getRecommendation("case-300");
        var draft = caseQueryService.previewDraft("case-300");

        assertThat(disruptionCase.caseId()).isEqualTo("case-300");
        assertThat(disruptionCase.impactedSegments()).hasSize(1);
        assertThat(recommendation.action()).isEqualTo("REBOOK");
        assertThat(recommendation.reasons()).hasSize(2);
        assertThat(draft.draftMessage()).contains("Alex Kim");
    }
}
