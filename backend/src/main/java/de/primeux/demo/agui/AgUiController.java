package de.primeux.demo.agui;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/** AG-UI endpoint: streams the supplement analysis run as AG-UI events over SSE. */
@RestController
@RequestMapping("/api/agui")
public class AgUiController {

    private final SupplementAnalysisService analysisService;

    public AgUiController(SupplementAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(value = "/analyze", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> analyze(@RequestBody RunAgentInput input) {
        return analysisService.analyze(input);
    }
}
