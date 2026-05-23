package de.primeux.demo.agui;

import java.util.Map;
import java.util.UUID;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;

/**
 * A server-side stand-in for a client-side AG-UI tool. The model can call it; instead of executing
 * anything, it streams the AG-UI TOOL_CALL_START/ARGS/END events to the client (which runs the real
 * tool, e.g. navigation) and returns a sentinel result so the model can continue.
 */
class ClientToolCallback implements ToolCallback {

    private final ToolDefinition definition;
    private final Sinks.Many<ServerSentEvent<Object>> events;

    ClientToolCallback(ToolDefinition definition, Sinks.Many<ServerSentEvent<Object>> events) {
        this.definition = definition;
        this.events = events;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return definition;
    }

    @Override
    public String call(String toolInput) {
        String id = UUID.randomUUID().toString();
        events.tryEmitNext(sse(AgUiEvents.toolCallStart(id, definition.name())));
        events.tryEmitNext(sse(AgUiEvents.toolCallArgs(id, toolInput == null ? "{}" : toolInput)));
        events.tryEmitNext(sse(AgUiEvents.toolCallEnd(id)));
        // No TOOL_CALL_RESULT: the client produces the result by executing the tool itself.
        return "{\"status\":\"handled by the UI\"}";
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        return call(toolInput);
    }

    private static ServerSentEvent<Object> sse(Map<String, Object> event) {
        return ServerSentEvent.builder().data(event).build();
    }
}
