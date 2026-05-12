package com.travelcx.recovery.contracts;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResponseSerializationTests {
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void serializesRecommendationResponse() throws Exception {
        RecommendationResponse response = new RecommendationResponse(
                "REBOOK",
                85,
                "Offer an alternative itinerary.",
                "Connection risk requires action.",
                new RecommendationContextResponse("HIGH", "60_MINUTES", false, true),
                List.of("Connection risk requires action."));

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"action\":\"REBOOK\"");
        assertThat(json).contains("\"score\":85");
    }

    @Test
    void serializesDisruptionCaseResponse() throws Exception {
        DisruptionCaseResponse response = new DisruptionCaseResponse(
                "case-001",
                "BR1234",
                "FLIGHT_DELAY",
                OffsetDateTime.of(2026, 5, 12, 10, 15, 0, 0, ZoneOffset.UTC),
                2,
                95,
                true,
                false,
                true,
                new CustomerProfileResponse("cust-1", "Taylor Rivera", "PRIME", false, false, true, false),
                List.of(new TripSegmentResponse(
                        "MAD",
                        "BCN",
                        "IB",
                        "IB411",
                        OffsetDateTime.of(2026, 5, 12, 11, 0, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 12, 12, 20, 0, 0, ZoneOffset.UTC))));

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"caseId\":\"case-001\"");
        assertThat(json).contains("\"highValueItinerary\":true");
        assertThat(json).contains("\"customer\"");
        assertThat(json).contains("\"impactedSegments\"");
    }
}
