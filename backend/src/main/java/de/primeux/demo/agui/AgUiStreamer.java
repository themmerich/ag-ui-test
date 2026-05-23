package de.primeux.demo.agui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Wraps a stream of model tokens into the AG-UI event lifecycle as SSE:
 * RUN_STARTED → TEXT_MESSAGE_START → TEXT_MESSAGE_CONTENT* → TEXT_MESSAGE_END → RUN_FINISHED,
 * or RUN_ERROR if the token stream fails. TEXT_MESSAGE_START is emitted lazily on the first token,
 * so events from other sources (e.g. tool calls) merged before the first token appear ahead of the
 * assistant's text.
 */
@Component
public class AgUiStreamer {

    public Flux<ServerSentEvent<Object>> stream(String threadId, String runId, Flux<String> tokens) {
        String messageId = UUID.randomUUID().toString();
        AtomicBoolean started = new AtomicBoolean(false);

        Flux<ServerSentEvent<Object>> body = tokens.flatMapIterable(token -> {
            if (token == null || token.isEmpty()) {
                return List.of();
            }
            List<ServerSentEvent<Object>> events = new ArrayList<>();
            if (started.compareAndSet(false, true)) {
                events.add(sse(AgUiEvents.textMessageStart(messageId, "assistant")));
            }
            events.add(sse(AgUiEvents.textMessageContent(messageId, token)));
            return events;
        });

        Flux<ServerSentEvent<Object>> closing = Flux.defer(() -> {
            List<ServerSentEvent<Object>> events = new ArrayList<>();
            if (started.get()) {
                events.add(sse(AgUiEvents.textMessageEnd(messageId)));
            }
            events.add(sse(AgUiEvents.runFinished(threadId, runId)));
            return Flux.fromIterable(events);
        });

        return Flux.concat(Flux.just(sse(AgUiEvents.runStarted(threadId, runId))), body, closing)
                .onErrorResume(e -> Flux.just(sse(AgUiEvents.runError(rootMessage(e)))));
    }

    static String rootMessage(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
    }

    static ServerSentEvent<Object> sse(Map<String, Object> event) {
        return ServerSentEvent.builder().data(event).build();
    }
}
