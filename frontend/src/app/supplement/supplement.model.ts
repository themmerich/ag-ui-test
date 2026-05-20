/** One row of the tracking table: a supplement and, per tracked day, whether it was taken. */
export interface SupplementRow {
  supplement: string;
  /** Aligned with {@link SupplementTracking.days} (same order). */
  taken: boolean[];
}

/** Response of the supplement tracking endpoint. */
export interface SupplementTracking {
  /** Tracked days as ISO date strings (oldest first), e.g. "2026-05-20". */
  days: string[];
  rows: SupplementRow[];
}
