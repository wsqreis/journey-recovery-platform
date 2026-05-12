package com.travelcx.recovery.contracts;

public record RecommendationEvent(String caseId, RecommendationResponse recommendation) {}
