package com.travelcx.recovery.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.RecommendationContextResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnthropicDraftMessageServiceTests {
    @Test
    void fallsBackWhenNoApiKeyIsAvailable() {
        FallbackDraftMessageService fallback = new FallbackDraftMessageService();
        AnthropicDraftingProperties properties = new AnthropicDraftingProperties(
                true,
                "claude-opus-4-7",
                new AnthropicDraftingProperties.Drafting("1h", 20));

        String draft = new AnthropicDraftMessageService(properties, fallback).draftMessage(sampleSnapshot());

        assertThat(draft).contains("Jordan Lee");
        assertThat(draft).contains("booking BR4000");
    }

    private CachedCaseSnapshot sampleSnapshot() {
        return new CachedCaseSnapshot(
                new DisruptionCaseResponse(
                        "case-400",
                        "BR4000",
                        "FLIGHT_DELAY",
                        OffsetDateTime.of(2026, 5, 12, 9, 0, 0, 0, ZoneOffset.UTC),
                        2,
                        180,
                        true,
                        false,
                        true,
                        new CustomerProfileResponse("cust-400", "Jordan Lee", "PRIME", false, false, true, false),
                        List.of()),
                new RecommendationResponse(
                        "REBOOK",
                        92,
                        "Offer an alternative itinerary before customer effort increases.",
                        "A missed connection risk makes itinerary recovery time-sensitive.",
                        new RecommendationContextResponse("HIGH", "60_MINUTES", false, true),
                        List.of("A missed connection risk makes itinerary recovery time-sensitive.")),
                "cacheable");
    }
}
