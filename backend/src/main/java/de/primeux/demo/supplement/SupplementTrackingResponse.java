package de.primeux.demo.supplement;

import java.util.List;

/**
 * Response of the supplement tracking endpoint.
 *
 * @param days tracked days as ISO date strings (oldest first), e.g. {@code "2026-05-20"}
 * @param rows one entry per supplement
 */
public record SupplementTrackingResponse(List<String> days, List<SupplementRow> rows) {
}
