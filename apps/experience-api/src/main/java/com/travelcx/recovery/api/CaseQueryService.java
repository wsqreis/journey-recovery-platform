package com.travelcx.recovery.api;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.DraftMessageResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import com.travelcx.recovery.contracts.TripSegmentResponse;
import com.travelcx.recovery.domain.CustomerProfile;
import com.travelcx.recovery.domain.DisruptionCase;
import com.travelcx.recovery.domain.DisruptionType;
import com.travelcx.recovery.domain.NextBestActionService;
import com.travelcx.recovery.domain.Recommendation;
import com.travelcx.recovery.domain.TripSegment;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CaseQueryService {
    private final NextBestActionService nextBestActionService;
    private final Map<String, DisruptionCase> cases;

    public CaseQueryService(NextBestActionService nextBestActionService) {
        this.nextBestActionService = nextBestActionService;
        this.cases = seedCases();
    }

    public DisruptionCaseResponse getCase(String caseId) {
        return toCaseResponse(findCase(caseId));
    }

    public RecommendationResponse getRecommendation(String caseId) {
        Recommendation recommendation = nextBestActionService.recommend(findCase(caseId));
        return toRecommendationResponse(recommendation);
    }

    public DraftMessageResponse previewDraft(String caseId) {
        DisruptionCase disruptionCase = findCase(caseId);
        Recommendation recommendation = nextBestActionService.recommend(disruptionCase);
        String draftMessage = "Hello %s, we detected a %s on booking %s. Our current recommendation is to %s. %s"
                .formatted(
                        disruptionCase.customerProfile().fullName(),
                        disruptionCase.disruptionType().name().toLowerCase().replace('_', ' '),
                        disruptionCase.bookingReference(),
                        recommendation.action().name().toLowerCase().replace('_', ' '),
                        recommendation.summary());
        return new DraftMessageResponse(disruptionCase.caseId(), draftMessage);
    }

    private DisruptionCase findCase(String caseId) {
        DisruptionCase disruptionCase = cases.get(caseId);
        if (disruptionCase == null) {
            throw new NoSuchElementException("Case not found: " + caseId);
        }
        return disruptionCase;
    }

    private DisruptionCaseResponse toCaseResponse(DisruptionCase disruptionCase) {
        return new DisruptionCaseResponse(
                disruptionCase.caseId(),
                disruptionCase.bookingReference(),
                disruptionCase.disruptionType().name(),
                disruptionCase.detectedAt(),
                disruptionCase.impactedPassengers(),
                disruptionCase.delayMinutes(),
                disruptionCase.connectionAtRisk(),
                disruptionCase.overnightImpact(),
                toCustomerResponse(disruptionCase.customerProfile()),
                disruptionCase.impactedSegments().stream().map(this::toSegmentResponse).toList());
    }

    private CustomerProfileResponse toCustomerResponse(CustomerProfile customerProfile) {
        return new CustomerProfileResponse(
                customerProfile.customerId(),
                customerProfile.fullName(),
                customerProfile.loyaltyTier(),
                customerProfile.travelingWithChildren(),
                customerProfile.requiresAccessibilitySupport());
    }

    private TripSegmentResponse toSegmentResponse(TripSegment tripSegment) {
        return new TripSegmentResponse(
                tripSegment.origin(),
                tripSegment.destination(),
                tripSegment.marketingCarrier(),
                tripSegment.flightNumber(),
                tripSegment.scheduledDepartureAt(),
                tripSegment.scheduledArrivalAt());
    }

    private RecommendationResponse toRecommendationResponse(Recommendation recommendation) {
        return new RecommendationResponse(
                recommendation.action().name(),
                recommendation.score(),
                recommendation.summary(),
                recommendation.explanation(),
                recommendation.reasons());
    }

    private Map<String, DisruptionCase> seedCases() {
        DisruptionCase caseOne = new DisruptionCase(
                "case-001",
                "BR1234",
                DisruptionType.FLIGHT_DELAY,
                OffsetDateTime.of(2026, 5, 12, 10, 15, 0, 0, ZoneOffset.UTC),
                2,
                95,
                true,
                false,
                new CustomerProfile("cust-1", "Taylor Rivera", "PRIME", false, false),
                List.of(new TripSegment(
                        "MAD",
                        "BCN",
                        "IB",
                        "IB411",
                        OffsetDateTime.of(2026, 5, 12, 11, 0, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 12, 12, 20, 0, 0, ZoneOffset.UTC))));

        DisruptionCase caseTwo = new DisruptionCase(
                "case-002",
                "ZX9012",
                DisruptionType.FLIGHT_CANCELLATION,
                OffsetDateTime.of(2026, 5, 12, 8, 40, 0, 0, ZoneOffset.UTC),
                3,
                260,
                true,
                true,
                new CustomerProfile("cust-2", "Alex Kim", "CLASSIC", true, false),
                List.of(new TripSegment(
                        "BCN",
                        "CDG",
                        "VY",
                        "VY8240",
                        OffsetDateTime.of(2026, 5, 12, 9, 10, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 12, 11, 5, 0, 0, ZoneOffset.UTC))));

        return Map.of(caseOne.caseId(), caseOne, caseTwo.caseId(), caseTwo);
    }
}
