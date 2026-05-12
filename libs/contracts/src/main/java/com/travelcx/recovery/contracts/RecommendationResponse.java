package com.travelcx.recovery.contracts;

import java.util.List;

public record RecommendationResponse(
        String action,
        int score,
        String summary,
        String explanation,
        RecommendationContextResponse context,
        List<String> reasons) {}
