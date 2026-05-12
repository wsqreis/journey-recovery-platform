package com.travelcx.recovery.api;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.DraftMessageResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import com.travelcx.recovery.contracts.TripSegmentResponse;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CaseQueryService {
    private final StoredDisruptionCaseStore disruptionCaseStore;
    private final StoredTripSegmentStore tripSegmentStore;

    public CaseQueryService(
            StoredDisruptionCaseStore disruptionCaseStore,
            StoredTripSegmentStore tripSegmentStore) {
        this.disruptionCaseStore = disruptionCaseStore;
        this.tripSegmentStore = tripSegmentStore;
    }

    public DisruptionCaseResponse getCase(String caseId) {
        StoredDisruptionCase storedDisruptionCase = findCase(caseId);
        return new DisruptionCaseResponse(
                storedDisruptionCase.caseId(),
                storedDisruptionCase.bookingReference(),
                storedDisruptionCase.disruptionType(),
                storedDisruptionCase.detectedAt(),
                storedDisruptionCase.impactedPassengers(),
                storedDisruptionCase.delayMinutes(),
                storedDisruptionCase.connectionAtRisk(),
                storedDisruptionCase.overnightImpact(),
                new CustomerProfileResponse(
                        storedDisruptionCase.customerId(),
                        storedDisruptionCase.customerFullName(),
                        storedDisruptionCase.loyaltyTier(),
                        storedDisruptionCase.travelingWithChildren(),
                        storedDisruptionCase.requiresAccessibilitySupport()),
                tripSegmentStore.findByCaseId(caseId).stream().map(this::toSegmentResponse).toList());
    }

    public RecommendationResponse getRecommendation(String caseId) {
        StoredDisruptionCase storedDisruptionCase = findCase(caseId);
        return new RecommendationResponse(
                storedDisruptionCase.recommendationAction(),
                storedDisruptionCase.recommendationScore(),
                storedDisruptionCase.recommendationSummary(),
                storedDisruptionCase.recommendationExplanation(),
                RecommendationReasons.parse(storedDisruptionCase.recommendationReasons()));
    }

    public DraftMessageResponse previewDraft(String caseId) {
        StoredDisruptionCase storedDisruptionCase = findCase(caseId);
        String draftMessage = "Hello %s, we detected a %s on booking %s. Our current recommendation is to %s. %s"
                .formatted(
                        storedDisruptionCase.customerFullName(),
                        storedDisruptionCase.disruptionType().toLowerCase().replace('_', ' '),
                        storedDisruptionCase.bookingReference(),
                        storedDisruptionCase.recommendationAction().toLowerCase().replace('_', ' '),
                        storedDisruptionCase.recommendationSummary());
        return new DraftMessageResponse(caseId, draftMessage);
    }

    private StoredDisruptionCase findCase(String caseId) {
        return disruptionCaseStore.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException("Case not found: " + caseId));
    }

    private TripSegmentResponse toSegmentResponse(StoredTripSegment storedTripSegment) {
        return new TripSegmentResponse(
                storedTripSegment.origin(),
                storedTripSegment.destination(),
                storedTripSegment.marketingCarrier(),
                storedTripSegment.flightNumber(),
                storedTripSegment.scheduledDepartureAt(),
                storedTripSegment.scheduledArrivalAt());
    }
}
