package de.primeux.demo.supplement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Provides in-memory test data for the supplement tracking. No persistence yet — the data is
 * generated deterministically (keyed by "days ago") so a given calendar day yields the same value
 * regardless of how many days are requested, and stays stable across requests.
 */
@Service
public class SupplementService {

    /** Size of the available test-data pool. */
    private static final int MAX_DAYS = 30;

    private static final int DEFAULT_DAYS = 7;

    private static final List<String> SUPPLEMENTS = List.of(
            "Magnesium 300mg",
            "Vitamin D3 2000 IE",
            "Omega-3 1000mg",
            "Vitamin C 500mg",
            "Zink 15mg");

    /** Tracking for the last {@value #DEFAULT_DAYS} days (used by the table). */
    public SupplementTrackingResponse getTracking() {
        return getTracking(DEFAULT_DAYS);
    }

    /** Tracking for the last {@code days} days (clamped to 1..{@value #MAX_DAYS}), oldest first. */
    public SupplementTrackingResponse getTracking(int days) {
        int span = Math.max(1, Math.min(MAX_DAYS, days));
        LocalDate today = LocalDate.now();

        List<String> dayLabels = new ArrayList<>(span);
        for (int daysAgo = span - 1; daysAgo >= 0; daysAgo--) {
            dayLabels.add(today.minusDays(daysAgo).toString());
        }

        List<SupplementRow> rows = new ArrayList<>(SUPPLEMENTS.size());
        for (int s = 0; s < SUPPLEMENTS.size(); s++) {
            List<Boolean> taken = new ArrayList<>(span);
            for (int daysAgo = span - 1; daysAgo >= 0; daysAgo--) {
                taken.add(isTaken(s, daysAgo));
            }
            rows.add(new SupplementRow(SUPPLEMENTS.get(s), taken));
        }
        return new SupplementTrackingResponse(dayLabels, rows);
    }

    /** Deterministic on/off pattern keyed by "days ago" (0 = today). */
    private boolean isTaken(int supplementIndex, int daysAgo) {
        return ((supplementIndex + 1) * 7 + daysAgo * 3) % 4 != 0;
    }
}
