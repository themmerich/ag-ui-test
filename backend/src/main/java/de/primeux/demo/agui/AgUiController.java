package de.primeux.demo.agui;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/** AG-UI endpoints: stream agent runs as AG-UI events over SSE. */
@RestController
@RequestMapping("/api/agui")
public class AgUiController {

    private final SupplementAnalysisService analysisService;
    private final ChatService chatService;

    public AgUiController(SupplementAnalysisService analysisService, ChatService chatService) {
        this.analysisService = analysisService;
        this.chatService = chatService;
    }

    /** Single-shot analysis of the supplement table (used by the "Analysieren" button). */
    @PostMapping(value = "/analyze", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> analyze(@RequestBody RunAgentInput input) {
        return analysisService.analyze(input);
    }

    /** Multi-turn chat with the agent (used by the chat sidebar). */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> chat(@RequestBody RunAgentInput input) {
        return chatService.chat(input);
    }
}
