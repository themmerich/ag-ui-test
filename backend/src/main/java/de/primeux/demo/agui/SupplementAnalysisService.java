package de.primeux.demo.agui;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Runs an AG-UI "run" for the supplement analysis: streams the model's answer as AG-UI events.
 * Single-shot — uses only the latest user message together with a fixed analysis system prompt.
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
    private final AgUiStreamer streamer;

    public SupplementAnalysisService(ChatClient.Builder chatClientBuilder, AgUiStreamer streamer) {
        this.chatClient = chatClientBuilder.build();
        this.streamer = streamer;
    }

    public Flux<ServerSentEvent<Object>> analyze(RunAgentInput input) {
        Flux<String> tokens = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(latestUserContent(input.messages()))
                .stream()
                .content();

        return streamer.stream(input.threadId(), input.runId(), tokens);
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
}
