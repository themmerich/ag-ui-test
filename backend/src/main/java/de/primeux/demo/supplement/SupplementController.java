package de.primeux.demo.supplement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Read-only REST API for the supplement tracking table. */
@RestController
@RequestMapping("/api/supplements")
public class SupplementController {

    private final SupplementService supplementService;

    public SupplementController(SupplementService supplementService) {
        this.supplementService = supplementService;
    }

    /** Tracking for the last {@code days} days (default 7, clamped to 1..30 by the service). */
    @GetMapping("/tracking")
    public SupplementTrackingResponse getTracking(
            @RequestParam(name = "days", defaultValue = "7") int days) {
        return supplementService.getTracking(days);
    }
}
