package com.travelcx.recovery.worker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class IdempotencyGuardTests {
    @Test
    void returnsTrueOnlyForFirstEventInsert() {
        StringRedisTemplate redisTemplate = Mockito.mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent("disruption-event:event-1", "processed", Duration.ofHours(6)))
                .willReturn(true);
        given(valueOperations.setIfAbsent("disruption-event:event-2", "processed", Duration.ofHours(6)))
                .willReturn(false);

        IdempotencyGuard idempotencyGuard = new IdempotencyGuard(redisTemplate);

        assertThat(idempotencyGuard.shouldProcess("event-1")).isTrue();
        assertThat(idempotencyGuard.shouldProcess("event-2")).isFalse();
    }
}
