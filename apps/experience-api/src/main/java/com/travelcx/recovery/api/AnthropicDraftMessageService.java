package com.travelcx.recovery.api;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.CacheControlEphemeral;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.TextBlockParam;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "anthropic", name = "enabled", havingValue = "true")
public class AnthropicDraftMessageService implements DraftMessageService {
    private final AnthropicClient anthropicClient;
    private final AnthropicDraftingProperties properties;
    private final FallbackDraftMessageService fallbackDraftMessageService;

    public AnthropicDraftMessageService(
            AnthropicDraftingProperties properties, FallbackDraftMessageService fallbackDraftMessageService) {
        this.anthropicClient = AnthropicOkHttpClient.fromEnv();
        this.properties = properties;
        this.fallbackDraftMessageService = fallbackDraftMessageService;
    }

    @Override
    public String draftMessage(CachedCaseSnapshot snapshot) {
        try {
            MessageCreateParams params = MessageCreateParams.builder()
                    .model(resolveModel(properties.model()))
                    .maxTokens(512L)
                    .systemOfTextBlockParams(List.of(TextBlockParam.builder()
                            .text("You draft concise customer-support travel disruption messages. Use plain English, preserve the recommendation, avoid compensation promises not present in the input, and keep the message to 3 sentences maximum.")
                            .cacheControl(CacheControlEphemeral.builder().build())
                            .build()))
                    .addUserMessage(buildUserPrompt(snapshot))
                    .build();

            Message message = anthropicClient.messages().create(params);
            return message.content().stream()
                    .filter(ContentBlock::isText)
                    .map(block -> block.asText().text())
                    .findFirst()
                    .filter(text -> !text.isBlank())
                    .orElseGet(() -> fallbackDraftMessageService.draftMessage(snapshot));
        } catch (RuntimeException exception) {
            return fallbackDraftMessageService.draftMessage(snapshot);
        }
    }

    private Model resolveModel(String configuredModel) {
        return Model.of(configuredModel == null || configuredModel.isBlank() ? "claude-opus-4-7" : configuredModel);
    }

    private String buildUserPrompt(CachedCaseSnapshot snapshot) {
        return "Customer: %s\nBooking: %s\nDisruption: %s\nPriority: %s\nRecommendation: %s\nRecommendation summary: %s\nReasons: %s\nReturn only the final message text."
                .formatted(
                        snapshot.disruptionCase().customer().fullName(),
                        snapshot.disruptionCase().bookingReference(),
                        snapshot.disruptionCase().disruptionType(),
                        snapshot.recommendation().context().priority(),
                        snapshot.recommendation().action(),
                        snapshot.recommendation().summary(),
                        String.join("; ", snapshot.recommendation().reasons()));
    }
}
