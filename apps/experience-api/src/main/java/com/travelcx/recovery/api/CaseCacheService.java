package com.travelcx.recovery.api;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import com.travelcx.recovery.contracts.TripSegmentResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CaseCacheService {
    private final StoredDisruptionCaseStore disruptionCaseStore;
    private final StoredTripSegmentStore tripSegmentStore;

    public CaseCacheService(StoredDisruptionCaseStore disruptionCaseStore, StoredTripSegmentStore tripSegmentStore) {
        this.disruptionCaseStore = disruptionCaseStore;
        this.tripSegmentStore = tripSegmentStore;
    }

    @Cacheable(cacheNames = "case-snapshots", key = "#caseId")
    public CachedCaseSnapshot getSnapshot(String caseId) {
        StoredDisruptionCase storedDisruptionCase = disruptionCaseStore
                .findById(caseId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Case not found: " + caseId));
        DisruptionCaseResponse disruptionCase = new DisruptionCaseResponse(
                storedDisruptionCase.caseId(),
                storedDisruptionCase.bookingReference(),
                storedDisruptionCase.disruptionType(),
                storedDisruptionCase.detectedAt(),
                storedDisruptionCase.impactedPassengers(),
                storedDisruptionCase.delayMinutes(),
                storedDisruptionCase.connectionAtRisk(),
                storedDisruptionCase.overnightImpact(),
                storedDisruptionCase.highValueItinerary(),
                new CustomerProfileResponse(
                        storedDisruptionCase.customerId(),
                        storedDisruptionCase.customerFullName(),
                        storedDisruptionCase.loyaltyTier(),
                        storedDisruptionCase.travelingWithChildren(),
                        storedDisruptionCase.requiresAccessibilitySupport(),
                        storedDisruptionCase.vipCustomer(),
                        storedDisruptionCase.corporateTraveler()),
                tripSegmentStore.findByCaseId(caseId).stream()
                        .map(segment -> new TripSegmentResponse(
                                segment.origin(),
                                segment.destination(),
                                segment.marketingCarrier(),
                                segment.flightNumber(),
                                segment.scheduledDepartureAt(),
                                segment.scheduledArrivalAt()))
                        .toList());
        RecommendationResponse recommendation = new RecommendationResponse(
                storedDisruptionCase.recommendationAction(),
                storedDisruptionCase.recommendationScore(),
                storedDisruptionCase.recommendationSummary(),
                storedDisruptionCase.recommendationExplanation(),
                RecommendationContextMapper.fromStoredCase(storedDisruptionCase),
                RecommendationReasons.parse(storedDisruptionCase.recommendationReasons()));
        return new CachedCaseSnapshot(disruptionCase, recommendation, "cacheable");
    }
}
