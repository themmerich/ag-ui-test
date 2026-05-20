package de.primeux.demo.supplement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read-only REST API for the supplement tracking table. */
@RestController
@RequestMapping("/api/supplements")
public class SupplementController {

    private final SupplementService supplementService;

    public SupplementController(SupplementService supplementService) {
        this.supplementService = supplementService;
    }

    @GetMapping("/tracking")
    public SupplementTrackingResponse getTracking() {
        return supplementService.getTracking();
    }
}
