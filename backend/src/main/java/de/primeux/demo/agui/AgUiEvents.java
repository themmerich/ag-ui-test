package de.primeux.demo.agui;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Factory methods for AG-UI protocol events. Each event is a plain map serialized to JSON (the SSE
 * {@code data:} payload) with a {@code type} discriminator and the field names defined by the
 * AG-UI spec (https://docs.ag-ui.com/concepts/events).
 */
public final class AgUiEvents {

    private AgUiEvents() {}

    public static Map<String, Object> runStarted(String threadId, String runId) {
        Map<String, Object> e = base("RUN_STARTED");
        e.put("threadId", threadId);
        e.put("runId", runId);
        return e;
    }

    public static Map<String, Object> textMessageStart(String messageId, String role) {
        Map<String, Object> e = base("TEXT_MESSAGE_START");
        e.put("messageId", messageId);
        e.put("role", role);
        return e;
    }

    public static Map<String, Object> textMessageContent(String messageId, String delta) {
        Map<String, Object> e = base("TEXT_MESSAGE_CONTENT");
        e.put("messageId", messageId);
        e.put("delta", delta);
        return e;
    }

    public static Map<String, Object> textMessageEnd(String messageId) {
        Map<String, Object> e = base("TEXT_MESSAGE_END");
        e.put("messageId", messageId);
        return e;
    }

    public static Map<String, Object> runFinished(String threadId, String runId) {
        Map<String, Object> e = base("RUN_FINISHED");
        e.put("threadId", threadId);
        e.put("runId", runId);
        return e;
    }

    public static Map<String, Object> runError(String message) {
        Map<String, Object> e = base("RUN_ERROR");
        e.put("message", message);
        return e;
    }

    private static Map<String, Object> base(String type) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("type", type);
        return e;
    }
}
