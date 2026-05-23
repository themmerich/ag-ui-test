package de.primeux.demo.supplement;

import java.util.List;

/**
 * A single day's supplement intake.
 *
 * @param date ISO date string ("2026-05-23")
 * @param existing whether data was already stored for this date (vs. a fresh, empty day)
 * @param supplements one entry per known supplement, with its taken state
 */
public record SupplementDay(String date, boolean existing, List<SupplementIntake> supplements) {
}
