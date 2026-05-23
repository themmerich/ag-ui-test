package de.primeux.demo.agui;

import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;

/**
 * Decorates a {@link ToolCallback} so that each invocation emits AG-UI TOOL_CALL_* events into a
 * side channel (the client renders them as info lines). Spring AI's internal tool execution does not
 * surface tool calls in the response stream, so we intercept the execution here.
 *
 * <p>Order is START → END → RESULT: the RESULT marks the call as completed on the client; an END
 * after it would otherwise reset the call to "pending" and the client would flag it as an error.
 */
class EmittingToolCallback implements ToolCallback {

    private final ToolCallback delegate;
    private final Sinks.Many<ServerSentEvent<Object>> events;

    EmittingToolCallback(ToolCallback delegate, Sinks.Many<ServerSentEvent<Object>> events) {
        this.delegate = delegate;
        this.events = events;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public String call(String toolInput) {
        return emitAround(() -> delegate.call(toolInput));
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        return emitAround(() -> delegate.call(toolInput, toolContext));
    }

    private String emitAround(Supplier<String> execution) {
        String id = UUID.randomUUID().toString();
        String name = delegate.getToolDefinition().name();
        events.tryEmitNext(sse(AgUiEvents.toolCallStart(id, name)));
        try {
            String result = execution.get();
            events.tryEmitNext(sse(AgUiEvents.toolCallEnd(id)));
            events.tryEmitNext(sse(AgUiEvents.toolCallResult(id, "ausgeführt")));
            return result;
        } catch (RuntimeException ex) {
            events.tryEmitNext(sse(AgUiEvents.toolCallEnd(id)));
            throw ex;
        }
    }

    private static ServerSentEvent<Object> sse(java.util.Map<String, Object> event) {
        return ServerSentEvent.builder().data(event).build();
    }
}
