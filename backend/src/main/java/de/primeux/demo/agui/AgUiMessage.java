package de.primeux.demo.agui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A single message from the AG-UI client (subset of the AG-UI message shape). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgUiMessage(String id, String role, String content) {
}
