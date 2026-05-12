package com.travelcx.recovery.worker;

import static org.assertj.core.api.Assertions.assertThat;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionEvent;
import com.travelcx.recovery.contracts.TripSegmentResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
class DecisionWorkerIntegrationTests {
    @Autowired
    private KafkaTemplate<String, DisruptionEvent> disruptionEventKafkaTemplate;

    @Autowired
    private StoredDisruptionCaseStore disruptionCaseStore;

    @Autowired
    private StoredTripSegmentStore tripSegmentStore;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        disruptionCaseStore.deleteAll();
        stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    @Test
    void consumesDisruptionEventAndPersistsRecommendation() {
        DisruptionEvent disruptionEvent = sampleEvent("event-001", "case-100");

        disruptionEventKafkaTemplate.send("disruption-events", disruptionEvent.caseId(), disruptionEvent);

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            StoredDisruptionCase storedDisruptionCase = disruptionCaseStore
                    .findById("case-100")
                    .orElseThrow();
            assertThat(storedDisruptionCase.recommendationAction()).isEqualTo("REBOOK");
            assertThat(tripSegmentStore.findByCaseId("case-100")).hasSize(1);
        });
    }

    @Test
    void ignoresDuplicateEventIds() {
        DisruptionEvent disruptionEvent = sampleEvent("event-duplicate", "case-200");

        disruptionEventKafkaTemplate.send("disruption-events", disruptionEvent.caseId(), disruptionEvent);
        disruptionEventKafkaTemplate.send("disruption-events", disruptionEvent.caseId(), disruptionEvent);

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            StoredDisruptionCase storedDisruptionCase = disruptionCaseStore
                    .findById("case-200")
                    .orElseThrow();
            assertThat(storedDisruptionCase.recommendationAction()).isEqualTo("REBOOK");
            assertThat(tripSegmentStore.findByCaseId("case-200")).hasSize(1);
        });
    }

    private DisruptionEvent sampleEvent(String eventId, String caseId) {
        return new DisruptionEvent(
                eventId,
                caseId,
                "BR9001",
                "FLIGHT_DELAY",
                OffsetDateTime.of(2026, 5, 12, 10, 15, 0, 0, ZoneOffset.UTC),
                2,
                120,
                true,
                false,
                new CustomerProfileResponse("cust-100", "Taylor Rivera", "PRIME", false, false),
                List.of(new TripSegmentResponse(
                        "MAD",
                        "BCN",
                        "IB",
                        "IB411",
                        OffsetDateTime.of(2026, 5, 12, 11, 0, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 12, 12, 20, 0, 0, ZoneOffset.UTC))));
    }
}
