package com.travelcx.recovery.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.RecommendationContextResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class CaseQueryServiceTests {
    @Test
    void formatsDraftFromCachedSnapshot() {
        CaseCacheService caseCacheService = org.mockito.Mockito.mock(CaseCacheService.class);
        CaseQueryService caseQueryService = new CaseQueryService(caseCacheService);
        CachedCaseSnapshot snapshot = new CachedCaseSnapshot(
                new DisruptionCaseResponse(
                        "case-300",
                        "BR3000",
                        "FLIGHT_CANCELLATION",
                        OffsetDateTime.of(2026, 5, 12, 9, 0, 0, 0, ZoneOffset.UTC),
                        3,
                        280,
                        true,
                        true,
                        true,
                        new CustomerProfileResponse("cust-300", "Alex Kim", "CLASSIC", true, false, true, false),
                        List.of()),
                new RecommendationResponse(
                        "REBOOK",
                        115,
                        "Offer an alternative itinerary before customer effort increases.",
                        "A missed connection risk makes itinerary recovery time-sensitive.",
                        new RecommendationContextResponse("CRITICAL", "15_MINUTES", true, true),
                        List.of("A missed connection risk makes itinerary recovery time-sensitive.")),
                "cacheable");
        given(caseCacheService.getSnapshot("case-300")).willReturn(snapshot);

        var draft = caseQueryService.previewDraft("case-300");

        assertThat(draft.caseId()).isEqualTo("case-300");
        assertThat(draft.draftMessage()).contains("Alex Kim");
        assertThat(draft.draftMessage()).contains("rebook");
    }
}
