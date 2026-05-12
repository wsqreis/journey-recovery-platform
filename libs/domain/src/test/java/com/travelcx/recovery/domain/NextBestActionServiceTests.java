package com.travelcx.recovery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class NextBestActionServiceTests {
    private final NextBestActionService nextBestActionService = new NextBestActionService();

    @Test
    void recommendsHotelForOvernightDisruption() {
        DisruptionCase disruptionCase = sampleCaseBuilder()
                .overnightImpact(true)
                .delayMinutes(240)
                .build();

        Recommendation recommendation = nextBestActionService.recommend(disruptionCase);

        assertThat(recommendation.action()).isEqualTo(RecommendationAction.REBOOK);
        assertThat(recommendation.reasons()).contains("An overnight impact requires accommodation support.");
    }

    @Test
    void recommendsEscalationForAccessibilityNeeds() {
        DisruptionCase disruptionCase = sampleCaseBuilder()
                .customerProfile(new CustomerProfile("cust-2", "Alex Kim", "CLASSIC", false, true))
                .build();

        Recommendation recommendation = nextBestActionService.recommend(disruptionCase);

        assertThat(recommendation.action()).isEqualTo(RecommendationAction.ESCALATE_TO_AGENT);
        assertThat(recommendation.explanation()).contains("Accessibility support requirements need specialist handling.");
    }

    @Test
    void recommendsRebookingForPrimeConnectionRisk() {
        DisruptionCase disruptionCase = sampleCaseBuilder()
                .connectionAtRisk(true)
                .customerProfile(new CustomerProfile("cust-3", "Morgan Singh", "PRIME", false, false))
                .build();

        Recommendation recommendation = nextBestActionService.recommend(disruptionCase);

        assertThat(recommendation.action()).isEqualTo(RecommendationAction.REBOOK);
        assertThat(recommendation.score()).isGreaterThan(50);
    }

    private SampleCaseBuilder sampleCaseBuilder() {
        return new SampleCaseBuilder();
    }

    private static final class SampleCaseBuilder {
        private String caseId = "case-001";
        private String bookingReference = "BR1234";
        private DisruptionType disruptionType = DisruptionType.FLIGHT_DELAY;
        private OffsetDateTime detectedAt = OffsetDateTime.of(2026, 5, 12, 10, 15, 0, 0, ZoneOffset.UTC);
        private int impactedPassengers = 2;
        private int delayMinutes = 95;
        private boolean connectionAtRisk;
        private boolean overnightImpact;
        private CustomerProfile customerProfile =
                new CustomerProfile("cust-1", "Taylor Rivera", "STANDARD", false, false);
        private List<TripSegment> impactedSegments = List.of(new TripSegment(
                "MAD",
                "BCN",
                "IB",
                "IB411",
                OffsetDateTime.of(2026, 5, 12, 11, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 5, 12, 12, 20, 0, 0, ZoneOffset.UTC)));

        private SampleCaseBuilder delayMinutes(int delayMinutes) {
            this.delayMinutes = delayMinutes;
            return this;
        }

        private SampleCaseBuilder connectionAtRisk(boolean connectionAtRisk) {
            this.connectionAtRisk = connectionAtRisk;
            return this;
        }

        private SampleCaseBuilder overnightImpact(boolean overnightImpact) {
            this.overnightImpact = overnightImpact;
            return this;
        }

        private SampleCaseBuilder customerProfile(CustomerProfile customerProfile) {
            this.customerProfile = customerProfile;
            return this;
        }

        private DisruptionCase build() {
            return new DisruptionCase(
                    caseId,
                    bookingReference,
                    disruptionType,
                    detectedAt,
                    impactedPassengers,
                    delayMinutes,
                    connectionAtRisk,
                    overnightImpact,
                    customerProfile,
                    impactedSegments);
        }
    }
}
