package de.primeux.demo.supplement;

import java.util.List;

/**
 * One row of the tracking table: a supplement and, for each tracked day (in the same order as
 * {@link SupplementTrackingResponse#days()}), whether it was taken.
 */
public record SupplementRow(String supplement, List<Boolean> taken) {
}
