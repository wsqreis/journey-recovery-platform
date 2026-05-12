package com.travelcx.recovery.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "anthropic")
public record AnthropicDraftingProperties(
        boolean enabled,
        String model,
        Drafting drafting) {
    public record Drafting(String promptCacheTtl, int timeoutSeconds) {}
}
