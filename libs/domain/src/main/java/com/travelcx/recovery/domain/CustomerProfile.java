package com.travelcx.recovery.domain;

public record CustomerProfile(
        String customerId,
        String fullName,
        String loyaltyTier,
        boolean travelingWithChildren,
        boolean requiresAccessibilitySupport,
        boolean vipCustomer,
        boolean corporateTraveler) {}
