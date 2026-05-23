package de.primeux.demo.supplement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Request body for updating/creating a day's supplement intake. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SupplementDayUpdate(List<SupplementIntake> supplements) {
}
