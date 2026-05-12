package com.travelcx.recovery.api;

import java.util.Arrays;
import java.util.List;

public final class RecommendationReasons {
    private RecommendationReasons() {}

    public static List<String> parse(String serializedReasons) {
        if (serializedReasons == null || serializedReasons.isBlank()) {
            return List.of();
        }
        return Arrays.stream(serializedReasons.split("\\|\\|", -1)).toList();
    }
}
