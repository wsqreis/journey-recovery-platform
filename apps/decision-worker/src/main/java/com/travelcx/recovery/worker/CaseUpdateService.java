package com.travelcx.recovery.worker;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionEvent;
import com.travelcx.recovery.contracts.RecommendationEvent;
import com.travelcx.recovery.contracts.RecommendationResponse;
import com.travelcx.recovery.contracts.TripSegmentResponse;
import com.travelcx.recovery.domain.CustomerProfile;
import com.travelcx.recovery.domain.DisruptionCase;
import com.travelcx.recovery.domain.DisruptionType;
import com.travelcx.recovery.domain.NextBestActionService;
import com.travelcx.recovery.domain.Recommendation;
import com.travelcx.recovery.domain.TripSegment;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CaseUpdateService {
    private final NextBestActionService nextBestActionService;
    private final StoredDisruptionCaseStore disruptionCaseStore;
    private final StoredTripSegmentStore tripSegmentStore;
    private final KafkaTemplate<String, RecommendationEvent> recommendationEventKafkaTemplate;

    public CaseUpdateService(
            NextBestActionService nextBestActionService,
            StoredDisruptionCaseStore disruptionCaseStore,
            StoredTripSegmentStore tripSegmentStore,
            KafkaTemplate<String, RecommendationEvent> recommendationEventKafkaTemplate) {
        this.nextBestActionService = nextBestActionService;
        this.disruptionCaseStore = disruptionCaseStore;
        this.tripSegmentStore = tripSegmentStore;
        this.recommendationEventKafkaTemplate = recommendationEventKafkaTemplate;
    }

    @Transactional
    public RecommendationResponse apply(DisruptionEvent disruptionEvent) {
        DisruptionCase disruptionCase = toDomain(disruptionEvent);
        Recommendation recommendation = nextBestActionService.recommend(disruptionCase);
        OffsetDateTime updatedAt = OffsetDateTime.now();

        disruptionCaseStore.upsert(toStoredCase(disruptionCase, recommendation, updatedAt));
        tripSegmentStore.replaceSegments(disruptionCase.caseId(), toStoredSegments(disruptionCase));

        RecommendationResponse recommendationResponse = new RecommendationResponse(
                recommendation.action().name(),
                recommendation.score(),
                recommendation.summary(),
                recommendation.explanation(),
                RecommendationContextMapper.fromRecommendation(recommendation),
                recommendation.reasons());
        recommendationEventKafkaTemplate.send(
                "recommendation-events",
                disruptionEvent.caseId(),
                new RecommendationEvent(disruptionEvent.caseId(), recommendationResponse));
        return recommendationResponse;
    }

    private DisruptionCase toDomain(DisruptionEvent disruptionEvent) {
        return new DisruptionCase(
                disruptionEvent.caseId(),
                disruptionEvent.bookingReference(),
                DisruptionType.valueOf(disruptionEvent.disruptionType()),
                disruptionEvent.detectedAt(),
                disruptionEvent.impactedPassengers(),
                disruptionEvent.delayMinutes(),
                disruptionEvent.connectionAtRisk(),
                disruptionEvent.overnightImpact(),
                disruptionEvent.highValueItinerary(),
                toCustomerProfile(disruptionEvent.customer()),
                disruptionEvent.impactedSegments().stream().map(this::toTripSegment).toList());
    }

    private CustomerProfile toCustomerProfile(CustomerProfileResponse customerProfileResponse) {
        return new CustomerProfile(
                customerProfileResponse.customerId(),
                customerProfileResponse.fullName(),
                customerProfileResponse.loyaltyTier(),
                customerProfileResponse.travelingWithChildren(),
                customerProfileResponse.requiresAccessibilitySupport(),
                customerProfileResponse.vipCustomer(),
                customerProfileResponse.corporateTraveler());
    }

    private TripSegment toTripSegment(TripSegmentResponse tripSegmentResponse) {
        return new TripSegment(
                tripSegmentResponse.origin(),
                tripSegmentResponse.destination(),
                tripSegmentResponse.marketingCarrier(),
                tripSegmentResponse.flightNumber(),
                tripSegmentResponse.scheduledDepartureAt(),
                tripSegmentResponse.scheduledArrivalAt());
    }

    private StoredDisruptionCase toStoredCase(
            DisruptionCase disruptionCase, Recommendation recommendation, OffsetDateTime updatedAt) {
        return new StoredDisruptionCase(
                disruptionCase.caseId(),
                disruptionCase.bookingReference(),
                disruptionCase.disruptionType().name(),
                disruptionCase.detectedAt(),
                disruptionCase.impactedPassengers(),
                disruptionCase.delayMinutes(),
                disruptionCase.connectionAtRisk(),
                disruptionCase.overnightImpact(),
                disruptionCase.highValueItinerary(),
                disruptionCase.customerProfile().customerId(),
                disruptionCase.customerProfile().fullName(),
                disruptionCase.customerProfile().loyaltyTier(),
                disruptionCase.customerProfile().travelingWithChildren(),
                disruptionCase.customerProfile().requiresAccessibilitySupport(),
                disruptionCase.customerProfile().vipCustomer(),
                disruptionCase.customerProfile().corporateTraveler(),
                recommendation.action().name(),
                recommendation.score(),
                recommendation.summary(),
                recommendation.explanation(),
                recommendation.context().priority().name(),
                recommendation.context().slaBucket(),
                recommendation.context().humanReviewRequired(),
                recommendation.context().premiumCustomer(),
                String.join("||", recommendation.reasons()),
                updatedAt);
    }

    private List<StoredTripSegment> toStoredSegments(DisruptionCase disruptionCase) {
        List<TripSegment> impactedSegments = disruptionCase.impactedSegments();
        return java.util.stream.IntStream.range(0, impactedSegments.size())
                .mapToObj(index -> {
                    TripSegment tripSegment = impactedSegments.get(index);
                    return new StoredTripSegment(
                            disruptionCase.caseId(),
                            index,
                            tripSegment.origin(),
                            tripSegment.destination(),
                            tripSegment.marketingCarrier(),
                            tripSegment.flightNumber(),
                            tripSegment.scheduledDepartureAt(),
                            tripSegment.scheduledArrivalAt());
                })
                .toList();
    }
}
