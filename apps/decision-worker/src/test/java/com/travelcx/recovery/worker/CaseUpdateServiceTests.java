package com.travelcx.recovery.worker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionEvent;
import com.travelcx.recovery.contracts.RecommendationEvent;
import com.travelcx.recovery.contracts.TripSegmentResponse;
import com.travelcx.recovery.domain.NextBestActionService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

class CaseUpdateServiceTests {
    private final StoredDisruptionCaseStore disruptionCaseStore = org.mockito.Mockito.mock(StoredDisruptionCaseStore.class);
    private final StoredTripSegmentStore tripSegmentStore = org.mockito.Mockito.mock(StoredTripSegmentStore.class);
    private final KafkaTemplate<String, RecommendationEvent> kafkaTemplate = org.mockito.Mockito.mock(KafkaTemplate.class);
    private final CaseUpdateService caseUpdateService =
            new CaseUpdateService(new NextBestActionService(), disruptionCaseStore, tripSegmentStore, kafkaTemplate);

    @Test
    void storesRichRecommendationMetadataAndPublishesEvent() {
        DisruptionEvent disruptionEvent = new DisruptionEvent(
                "event-001",
                "case-001",
                "BR1234",
                "FLIGHT_DELAY",
                OffsetDateTime.of(2026, 5, 12, 10, 15, 0, 0, ZoneOffset.UTC),
                2,
                220,
                true,
                true,
                true,
                new CustomerProfileResponse("cust-1", "Taylor Rivera", "PRIME", false, false, true, true),
                List.of(new TripSegmentResponse(
                        "MAD",
                        "BCN",
                        "IB",
                        "IB411",
                        OffsetDateTime.of(2026, 5, 12, 11, 0, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 12, 12, 20, 0, 0, ZoneOffset.UTC))));

        RecommendationEvent sendResult = new RecommendationEvent("case-001", null);
        willReturn(null).given(kafkaTemplate).send(eq("recommendation-events"), eq("case-001"), any(RecommendationEvent.class));

        var recommendation = caseUpdateService.apply(disruptionEvent);

        ArgumentCaptor<StoredDisruptionCase> caseCaptor = ArgumentCaptor.forClass(StoredDisruptionCase.class);
        then(disruptionCaseStore).should().upsert(caseCaptor.capture());
        StoredDisruptionCase storedDisruptionCase = caseCaptor.getValue();
        assertThat(storedDisruptionCase.recommendationPriority()).isEqualTo("CRITICAL");
        assertThat(storedDisruptionCase.recommendationSlaBucket()).isEqualTo("15_MINUTES");
        assertThat(storedDisruptionCase.recommendationPremiumCustomer()).isTrue();
        assertThat(storedDisruptionCase.highValueItinerary()).isTrue();
        assertThat(storedDisruptionCase.vipCustomer()).isTrue();
        assertThat(recommendation.context().priority()).isEqualTo("CRITICAL");
        assertThat(recommendation.context().humanReviewRequired()).isTrue();

        then(tripSegmentStore).should().replaceSegments(eq("case-001"), any());
        then(kafkaTemplate).should().send(eq("recommendation-events"), eq("case-001"), any(RecommendationEvent.class));
    }
}
