package de.primeux.demo.supplement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Provides in-memory test data for the supplement tracking table. No persistence yet — the data is
 * generated deterministically so it stays stable across requests.
 */
@Service
public class SupplementService {

    private static final int DAYS = 7;

    private static final List<String> SUPPLEMENTS = List.of(
            "Magnesium 300mg",
            "Vitamin D3 2000 IE",
            "Omega-3 1000mg",
            "Vitamin C 500mg",
            "Zink 15mg");

    public SupplementTrackingResponse getTracking() {
        List<String> days = buildDays();
        List<SupplementRow> rows = new ArrayList<>(SUPPLEMENTS.size());
        for (int s = 0; s < SUPPLEMENTS.size(); s++) {
            List<Boolean> taken = new ArrayList<>(DAYS);
            for (int d = 0; d < DAYS; d++) {
                taken.add(isTaken(s, d));
            }
            rows.add(new SupplementRow(SUPPLEMENTS.get(s), taken));
        }
        return new SupplementTrackingResponse(days, rows);
    }

    /** The last {@value #DAYS} days ending today, oldest first, as ISO date strings. */
    private List<String> buildDays() {
        LocalDate today = LocalDate.now();
        List<String> days = new ArrayList<>(DAYS);
        for (int i = DAYS - 1; i >= 0; i--) {
            days.add(today.minusDays(i).toString());
        }
        return days;
    }

    /** Deterministic on/off pattern so the demo data does not change between requests. */
    private boolean isTaken(int supplementIndex, int dayIndex) {
        return ((supplementIndex + 1) * 7 + dayIndex * 3) % 4 != 0;
    }
}
