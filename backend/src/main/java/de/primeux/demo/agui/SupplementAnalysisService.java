package de.primeux.demo.agui;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Runs an AG-UI "run" for the supplement analysis: streams the model's answer as AG-UI events
 * (RUN_STARTED → TEXT_MESSAGE_START → TEXT_MESSAGE_CONTENT* → TEXT_MESSAGE_END → RUN_FINISHED),
 * or RUN_ERROR if the model call fails (e.g. missing API key).
 */
@Service
public class SupplementAnalysisService {

    private static final String SYSTEM_PROMPT =
            """
            Du bist ein nüchterner Gesundheits-Coach. Du bekommst eine Übersicht, welche \
            Nahrungsergänzungsmittel an welchen Tagen genommen wurden. Analysiere kurz und konkret \
            auf Deutsch: Wie konsistent ist die Einnahme, welche Auffälligkeiten/Lücken gibt es, \
            und gib 1–3 knappe, umsetzbare Hinweise. Antworte in wenigen Sätzen, keine medizinischen \
            Heilversprechen.""";

    private final ChatClient chatClient;

    public SupplementAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public Flux<ServerSentEvent<Object>> analyze(RunAgentInput input) {
        String userText = latestUserContent(input.messages());
        String messageId = UUID.randomUUID().toString();

        Flux<ServerSentEvent<Object>> start = Flux.just(
                sse(AgUiEvents.runStarted(input.threadId(), input.runId())),
                sse(AgUiEvents.textMessageStart(messageId, "assistant")));

        Flux<ServerSentEvent<Object>> content = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(userText)
                .stream()
                .content()
                .map(delta -> sse(AgUiEvents.textMessageContent(messageId, delta)));

        Flux<ServerSentEvent<Object>> end = Flux.just(
                sse(AgUiEvents.textMessageEnd(messageId)),
                sse(AgUiEvents.runFinished(input.threadId(), input.runId())));

        return Flux.concat(start, content, end)
                .onErrorResume(e -> Flux.just(sse(AgUiEvents.runError(rootMessage(e)))));
    }

    private static String latestUserContent(List<AgUiMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        for (int i = messages.size() - 1; i >= 0; i--) {
            AgUiMessage m = messages.get(i);
            if ("user".equalsIgnoreCase(m.role()) && m.content() != null) {
                return m.content();
            }
        }
        return messages.get(messages.size() - 1).content();
    }

    private static String rootMessage(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
    }

    private static ServerSentEvent<Object> sse(Map<String, Object> event) {
        return ServerSentEvent.builder().data(event).build();
    }
}
