package de.primeux.demo.agui;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
            Gesprächsverlauf. Keine medizinischen Heilversprechen; bei ernsten Beschwerden zu \
            ärztlichem Rat raten.""";

    private final ChatClient chatClient;
    private final AgUiStreamer streamer;

    public ChatService(ChatClient.Builder chatClientBuilder, AgUiStreamer streamer) {
        this.chatClient = chatClientBuilder.build();
        this.streamer = streamer;
    }

    public Flux<ServerSentEvent<Object>> chat(RunAgentInput input) {
        Flux<String> tokens = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .messages(toSpringMessages(input.messages()))
                .stream()
                .content();

        return streamer.stream(input.threadId(), input.runId(), tokens);
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
