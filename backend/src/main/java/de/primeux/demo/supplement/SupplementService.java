package de.primeux.demo.supplement;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * In-memory supplement intake store. Seeded with {@value #MAX_DAYS} days of deterministic test data
 * on startup; edits via {@link #saveDay} overwrite a day. No persistence yet — resets on restart.
 */
@Service
public class SupplementService {

    private static final int MAX_DAYS = 30;
    private static final int DEFAULT_DAYS = 7;

    private static final List<String> SUPPLEMENTS = List.of(
            "Magnesium 300mg",
            "Vitamin D3 2000 IE",
            "Omega-3 1000mg",
            "Vitamin C 500mg",
            "Zink 15mg");

    /** date -> names of supplements taken that day. */
    private final Map<LocalDate, Set<String>> intake = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        LocalDate today = LocalDate.now();
        for (int daysAgo = 0; daysAgo < MAX_DAYS; daysAgo++) {
            Set<String> taken = new LinkedHashSet<>();
            for (int s = 0; s < SUPPLEMENTS.size(); s++) {
                if (isTaken(s, daysAgo)) {
                    taken.add(SUPPLEMENTS.get(s));
                }
            }
            intake.put(today.minusDays(daysAgo), taken);
        }
    }

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
        for (String supplement : SUPPLEMENTS) {
            List<Boolean> taken = new ArrayList<>(span);
            for (int daysAgo = span - 1; daysAgo >= 0; daysAgo--) {
                taken.add(takenOn(today.minusDays(daysAgo)).contains(supplement));
            }
            rows.add(new SupplementRow(supplement, taken));
        }
        return new SupplementTrackingResponse(dayLabels, rows);
    }

    /** A single day's intake; {@code existing=false} if nothing was stored for that date yet. */
    public SupplementDay getDay(LocalDate date) {
        Set<String> taken = takenOn(date);
        boolean existing = intake.containsKey(date);
        List<SupplementIntake> supplements = new ArrayList<>(SUPPLEMENTS.size());
        for (String supplement : SUPPLEMENTS) {
            supplements.add(new SupplementIntake(supplement, taken.contains(supplement)));
        }
        return new SupplementDay(date.toString(), existing, supplements);
    }

    /** Replaces the intake for {@code date} with the given selection and returns the stored day. */
    public SupplementDay saveDay(LocalDate date, List<SupplementIntake> supplements) {
        Set<String> taken = new LinkedHashSet<>();
        if (supplements != null) {
            for (SupplementIntake entry : supplements) {
                if (entry.taken() && SUPPLEMENTS.contains(entry.supplement())) {
                    taken.add(entry.supplement());
                }
            }
        }
        intake.put(date, taken);
        return getDay(date);
    }

    private Set<String> takenOn(LocalDate date) {
        return intake.getOrDefault(date, Set.of());
    }

    /** Deterministic on/off pattern keyed by "days ago" (0 = today), used only for seeding. */
    private boolean isTaken(int supplementIndex, int daysAgo) {
        return ((supplementIndex + 1) * 7 + daysAgo * 3) % 4 != 0;
    }
}
