package de.primeux.demo.agui;

import java.util.ArrayList;
import java.util.List;
import de.primeux.demo.supplement.SupplementQueryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Multi-turn chat with the agent: maps the full AG-UI message history (user/assistant) to Spring AI
 * messages so the model keeps conversational context, and streams the reply as AG-UI events.
 */
@Service
public class ChatService {

    private static final String SYSTEM_PROMPT =
            """
            Du bist ein hilfreicher Assistent rund um Gesundheit und Nahrungsergänzung. Antworte \
            knapp, freundlich und auf Deutsch. Beziehe dich bei Rückfragen auf den bisherigen \
            Gesprächsverlauf. Wenn der Nutzer nach seiner tatsächlichen Einnahme fragt (z. B. welche \
            Supplements er genommen oder ausgelassen hat), rufe das Tool `getSupplementIntake` mit der \
            passenden Anzahl Tage auf und beziehe dich auf die zurückgelieferten Daten. Keine \
            medizinischen Heilversprechen; bei ernsten Beschwerden zu ärztlichem Rat raten.""";

    private final ChatClient chatClient;
    private final AgUiStreamer streamer;
    private final SupplementQueryService supplementQueryService;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            AgUiStreamer streamer,
            SupplementQueryService supplementQueryService) {
        this.chatClient = chatClientBuilder.build();
        this.streamer = streamer;
        this.supplementQueryService = supplementQueryService;
    }

    public Flux<ServerSentEvent<Object>> chat(RunAgentInput input) {
        // Side channel for tool-call events: Spring AI executes tools internally and does not surface
        // them in the response stream, so we wrap the tool callbacks to emit AG-UI TOOL_CALL_* events.
        Sinks.Many<ServerSentEvent<Object>> toolEvents = Sinks.many().unicast().onBackpressureBuffer();

        ToolCallback[] base = MethodToolCallbackProvider.builder()
                .toolObjects(supplementQueryService)
                .build()
                .getToolCallbacks();
        List<ToolCallback> wrapped = new ArrayList<>(base.length);
        for (ToolCallback callback : base) {
            wrapped.add(new EmittingToolCallback(callback, toolEvents));
        }

        Flux<String> tokens = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .toolCallbacks(wrapped)
                .messages(toSpringMessages(input.messages()))
                .stream()
                .content();

        Flux<ServerSentEvent<Object>> textEvents = streamer
                .stream(input.threadId(), input.runId(), tokens)
                .doFinally(signal -> toolEvents.tryEmitComplete());

        return Flux.merge(toolEvents.asFlux(), textEvents);
    }

    private static List<Message> toSpringMessages(List<AgUiMessage> messages) {
        List<Message> result = new ArrayList<>();
        if (messages == null) {
            return result;
        }
        for (AgUiMessage m : messages) {
            if (m.content() == null || m.role() == null) {
                continue;
            }
            switch (m.role().toLowerCase()) {
                case "user" -> result.add(new UserMessage(m.content()));
                case "assistant" -> result.add(new AssistantMessage(m.content()));
                default -> {
                    // ignore system/other roles — the system prompt is set separately
                }
            }
        }
        return result;
    }
}
