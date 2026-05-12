package com.travelcx.recovery.worker;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class IdempotencyGuard {
    private static final Duration TTL = Duration.ofHours(6);
    private final StringRedisTemplate stringRedisTemplate;

    public IdempotencyGuard(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean shouldProcess(String eventId) {
        Boolean stored = stringRedisTemplate.opsForValue().setIfAbsent(key(eventId), "processed", TTL);
        return Boolean.TRUE.equals(stored);
    }

    private String key(String eventId) {
        return "disruption-event:" + eventId;
    }
}
