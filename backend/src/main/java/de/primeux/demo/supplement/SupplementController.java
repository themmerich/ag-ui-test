package de.primeux.demo.supplement;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST API for the supplement tracking table and per-day editing. */
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

    /** A single day's intake (existing=false with all supplements unchecked if not yet recorded). */
    @GetMapping("/day/{date}")
    public SupplementDay getDay(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return supplementService.getDay(date);
    }

    /** Creates or updates a day's intake. */
    @PutMapping("/day/{date}")
    public SupplementDay saveDay(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody SupplementDayUpdate update) {
        return supplementService.saveDay(date, update.supplements());
    }
}
