package de.primeux.demo.agui;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Wraps a stream of model tokens into the AG-UI event lifecycle as SSE:
 * RUN_STARTED → TEXT_MESSAGE_START → TEXT_MESSAGE_CONTENT* → TEXT_MESSAGE_END → RUN_FINISHED,
 * or RUN_ERROR if the token stream fails (e.g. missing API key).
 */
@Component
public class AgUiStreamer {

    public Flux<ServerSentEvent<Object>> stream(String threadId, String runId, Flux<String> tokens) {
        String messageId = UUID.randomUUID().toString();

        Flux<ServerSentEvent<Object>> start = Flux.just(
                sse(AgUiEvents.runStarted(threadId, runId)),
                sse(AgUiEvents.textMessageStart(messageId, "assistant")));

        Flux<ServerSentEvent<Object>> content =
                tokens.map(delta -> sse(AgUiEvents.textMessageContent(messageId, delta)));

        Flux<ServerSentEvent<Object>> end = Flux.just(
                sse(AgUiEvents.textMessageEnd(messageId)),
                sse(AgUiEvents.runFinished(threadId, runId)));

        return Flux.concat(start, content, end)
                .onErrorResume(e -> Flux.just(sse(AgUiEvents.runError(rootMessage(e)))));
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
