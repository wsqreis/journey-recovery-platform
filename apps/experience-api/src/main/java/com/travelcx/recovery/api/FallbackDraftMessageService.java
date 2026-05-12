package com.travelcx.recovery.api;

import org.springframework.stereotype.Service;

@Service
public class FallbackDraftMessageService implements DraftMessageService {
    @Override
    public String draftMessage(CachedCaseSnapshot snapshot) {
        return "Hello %s, we detected a %s on booking %s. Our current recommendation is to %s. %s"
                .formatted(
                        snapshot.disruptionCase().customer().fullName(),
                        snapshot.disruptionCase().disruptionType().toLowerCase().replace('_', ' '),
                        snapshot.disruptionCase().bookingReference(),
                        snapshot.recommendation().action().toLowerCase().replace('_', ' '),
                        snapshot.recommendation().summary());
    }
}
