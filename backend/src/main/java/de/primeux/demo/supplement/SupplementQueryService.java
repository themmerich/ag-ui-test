package de.primeux.demo.supplement;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * Query access to the supplement intake data, also exposed as a Spring AI tool so the chat agent can
 * look up the user's actual intake for the last N days.
 */
@Service
public class SupplementQueryService {

    private final SupplementService supplementService;

    public SupplementQueryService(SupplementService supplementService) {
        this.supplementService = supplementService;
    }

    @Tool(
            description =
                    "Liefert die protokollierte Supplement-Einnahme des Nutzers für die letzten N Tage: "
                            + "welche Supplements an welchen Tagen genommen (true) oder ausgelassen "
                            + "(false) wurden.")
    public SupplementTrackingResponse getSupplementIntake(
            @ToolParam(description = "Anzahl der letzten Tage, 1 bis 30") int days) {
        return supplementService.getTracking(days);
    }
}
