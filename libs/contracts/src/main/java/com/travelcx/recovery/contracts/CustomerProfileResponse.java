package com.travelcx.recovery.contracts;

public record CustomerProfileResponse(
        String customerId,
        String fullName,
        String loyaltyTier,
        boolean travelingWithChildren,
        boolean requiresAccessibilitySupport,
        boolean vipCustomer,
        boolean corporateTraveler) {}
