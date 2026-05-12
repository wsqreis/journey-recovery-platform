package com.travelcx.recovery.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class NextBestActionService {
    public Recommendation recommend(DisruptionCase disruptionCase) {
        Map<RecommendationAction, Integer> scores = baseScores(disruptionCase.disruptionType());
        List<String> reasons = new ArrayList<>();
        int urgencySignals = 0;

        if (disruptionCase.delayMinutes() >= 180) {
            increment(scores, RecommendationAction.REBOOK, 25);
            urgencySignals++;
            reasons.add("The disruption exceeds three hours, increasing the need to secure alternative travel.");
        }

        if (disruptionCase.connectionAtRisk()) {
            increment(scores, RecommendationAction.REBOOK, 20);
            increment(scores, RecommendationAction.ESCALATE_TO_AGENT, 10);
            urgencySignals++;
            reasons.add("A missed connection risk makes itinerary recovery time-sensitive.");
        }

        if (disruptionCase.overnightImpact()) {
            increment(scores, RecommendationAction.HOTEL, 30);
            urgencySignals++;
            reasons.add("An overnight impact requires accommodation support.");
        }

        if (disruptionCase.highValueItinerary()) {
            increment(scores, RecommendationAction.REBOOK, 15);
            reasons.add("High-value itinerary protection favors a proactive recovery path.");
        }

        CustomerProfile customerProfile = disruptionCase.customerProfile();
        boolean premiumCustomer = isPremium(customerProfile);

        if (premiumCustomer) {
            increment(scores, RecommendationAction.REBOOK, 12);
            reasons.add("Premium customer status raises the importance of a fast self-serve recovery outcome.");
        }

        if (customerProfile.travelingWithChildren()) {
            increment(scores, RecommendationAction.ESCALATE_TO_AGENT, 15);
            urgencySignals++;
            reasons.add("Family travel increases the value of guided assistance.");
        }

        if (customerProfile.requiresAccessibilitySupport()) {
            increment(scores, RecommendationAction.ESCALATE_TO_AGENT, 30);
            urgencySignals++;
            reasons.add("Accessibility support requirements need specialist handling.");
        }

        if (customerProfile.vipCustomer()) {
            increment(scores, RecommendationAction.ESCALATE_TO_AGENT, 12);
            reasons.add("VIP handling raises the need for a curated recovery experience.");
        }

        if (customerProfile.corporateTraveler()) {
            increment(scores, RecommendationAction.REBOOK, 8);
            reasons.add("Corporate travel favors itinerary continuity over compensation-first handling.");
        }

        if (disruptionCase.disruptionType() == DisruptionType.FLIGHT_CANCELLATION) {
            increment(scores, RecommendationAction.REBOOK, 15);
            increment(scores, RecommendationAction.VOUCHER, 10);
            reasons.add("A cancellation often requires either re-accommodation or compensation handling.");
        }

        RecommendationAction action = scores.entrySet().stream()
                .max(Comparator.<Map.Entry<RecommendationAction, Integer>>comparingInt(Map.Entry::getValue)
                        .thenComparing(entry -> entry.getKey().ordinal(), Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .orElse(RecommendationAction.ESCALATE_TO_AGENT);

        int score = scores.get(action);
        RecommendationPriority priority = determinePriority(urgencySignals, premiumCustomer, score);
        String slaBucket = switch (priority) {
            case CRITICAL -> "15_MINUTES";
            case HIGH -> "60_MINUTES";
            case STANDARD -> "4_HOURS";
        };
        boolean humanReviewRequired = action == RecommendationAction.ESCALATE_TO_AGENT
                || disruptionCase.customerProfile().requiresAccessibilitySupport()
                || priority == RecommendationPriority.CRITICAL;
        RecommendationContext context = new RecommendationContext(priority, slaBucket, humanReviewRequired, premiumCustomer);

        String summary = switch (action) {
            case REBOOK -> "Offer an alternative itinerary before customer effort increases.";
            case VOUCHER -> "Offer compensation while keeping the case in an automated flow.";
            case HOTEL -> "Provide accommodation support for the overnight disruption.";
            case ESCALATE_TO_AGENT -> "Route the case to a specialist for manual handling.";
        };
        String explanation = String.join(" ", reasons);

        return new Recommendation(action, score, summary, explanation, context, List.copyOf(reasons));
    }

    private Map<RecommendationAction, Integer> baseScores(DisruptionType disruptionType) {
        Map<RecommendationAction, Integer> scores = new EnumMap<>(RecommendationAction.class);
        scores.put(RecommendationAction.REBOOK, disruptionType == DisruptionType.FLIGHT_CANCELLATION ? 55 : 40);
        scores.put(RecommendationAction.VOUCHER, 20);
        scores.put(RecommendationAction.HOTEL, 5);
        scores.put(RecommendationAction.ESCALATE_TO_AGENT, 15);
        return scores;
    }

    private RecommendationPriority determinePriority(int urgencySignals, boolean premiumCustomer, int score) {
        if (urgencySignals >= 3 || score >= 100) {
            return RecommendationPriority.CRITICAL;
        }
        if (urgencySignals >= 2 || premiumCustomer || score >= 70) {
            return RecommendationPriority.HIGH;
        }
        return RecommendationPriority.STANDARD;
    }

    private boolean isPremium(CustomerProfile customerProfile) {
        return "PRIME".equalsIgnoreCase(customerProfile.loyaltyTier()) || customerProfile.vipCustomer();
    }

    private void increment(Map<RecommendationAction, Integer> scores, RecommendationAction action, int amount) {
        scores.compute(action, (key, value) -> value == null ? amount : value + amount);
    }
}
