package de.primeux.demo.agui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * A client-side tool offered by the AG-UI client (executed in the browser, not on the server).
 *
 * @param name tool name
 * @param description what the tool does (used by the model to decide when to call it)
 * @param parameters JSON Schema of the tool arguments
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClientTool(String name, String description, Map<String, Object> parameters) {
}
