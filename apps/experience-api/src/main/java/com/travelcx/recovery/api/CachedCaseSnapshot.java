package com.travelcx.recovery.api;

import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;

public record CachedCaseSnapshot(
        DisruptionCaseResponse disruptionCase,
        RecommendationResponse recommendation,
        String cacheStatus) {}
