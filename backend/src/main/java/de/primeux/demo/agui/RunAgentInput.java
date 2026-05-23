package de.primeux.demo.agui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Body sent by the AG-UI {@code HttpAgent} when starting a run. Only the fields we need are mapped;
 * the client sends more (state, context, forwardedProps, resume) — those are ignored.
 *
 * @param tools client-side tools offered for this run (the model may call them; the browser executes them)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RunAgentInput(
        String threadId, String runId, List<AgUiMessage> messages, List<ClientTool> tools) {
}
