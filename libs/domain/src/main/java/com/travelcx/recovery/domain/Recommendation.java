package com.travelcx.recovery.domain;

import java.util.List;

public record Recommendation(
        RecommendationAction action,
        int score,
        String summary,
        String explanation,
        List<String> reasons) {}
