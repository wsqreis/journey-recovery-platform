package com.travelcx.recovery.worker;

import com.travelcx.recovery.contracts.DisruptionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DisruptionEventConsumer {
    private final IdempotencyGuard idempotencyGuard;
    private final CaseUpdateService caseUpdateService;

    public DisruptionEventConsumer(IdempotencyGuard idempotencyGuard, CaseUpdateService caseUpdateService) {
        this.idempotencyGuard = idempotencyGuard;
        this.caseUpdateService = caseUpdateService;
    }

    @KafkaListener(topics = "disruption-events")
    public void onDisruptionEvent(DisruptionEvent disruptionEvent) {
        if (!idempotencyGuard.shouldProcess(disruptionEvent.eventId())) {
            return;
        }
        caseUpdateService.apply(disruptionEvent);
    }
}
