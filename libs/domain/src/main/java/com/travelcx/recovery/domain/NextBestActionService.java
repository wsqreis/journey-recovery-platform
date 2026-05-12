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

        if (disruptionCase.delayMinutes() >= 180) {
            increment(scores, RecommendationAction.REBOOK, 25);
            reasons.add("The disruption exceeds three hours, increasing the need to secure alternative travel.");
        }

        if (disruptionCase.connectionAtRisk()) {
            increment(scores, RecommendationAction.REBOOK, 20);
            increment(scores, RecommendationAction.ESCALATE_TO_AGENT, 5);
            reasons.add("A missed connection risk makes itinerary recovery time-sensitive.");
        }

        if (disruptionCase.overnightImpact()) {
            increment(scores, RecommendationAction.HOTEL, 30);
            reasons.add("An overnight impact requires accommodation support.");
        }

        if ("PRIME".equalsIgnoreCase(disruptionCase.customerProfile().loyaltyTier())) {
            increment(scores, RecommendationAction.REBOOK, 10);
            reasons.add("Premium loyalty status favors a proactive recovery option.");
        }

        if (disruptionCase.customerProfile().travelingWithChildren()) {
            increment(scores, RecommendationAction.ESCALATE_TO_AGENT, 15);
            reasons.add("Family travel increases the value of guided assistance.");
        }

        if (disruptionCase.customerProfile().requiresAccessibilitySupport()) {
            increment(scores, RecommendationAction.ESCALATE_TO_AGENT, 30);
            reasons.add("Accessibility support requirements need specialist handling.");
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
        String summary = switch (action) {
            case REBOOK -> "Offer an alternative itinerary before customer effort increases.";
            case VOUCHER -> "Offer compensation while keeping the case in an automated flow.";
            case HOTEL -> "Provide accommodation support for the overnight disruption.";
            case ESCALATE_TO_AGENT -> "Route the case to a specialist for manual handling.";
        };
        String explanation = String.join(" ", reasons);

        return new Recommendation(action, score, summary, explanation, List.copyOf(reasons));
    }

    private Map<RecommendationAction, Integer> baseScores(DisruptionType disruptionType) {
        Map<RecommendationAction, Integer> scores = new EnumMap<>(RecommendationAction.class);
        scores.put(RecommendationAction.REBOOK, disruptionType == DisruptionType.FLIGHT_CANCELLATION ? 55 : 40);
        scores.put(RecommendationAction.VOUCHER, 20);
        scores.put(RecommendationAction.HOTEL, 5);
        scores.put(RecommendationAction.ESCALATE_TO_AGENT, 15);
        return scores;
    }

    private void increment(Map<RecommendationAction, Integer> scores, RecommendationAction action, int amount) {
        scores.compute(action, (key, value) -> value == null ? amount : value + amount);
    }
}
