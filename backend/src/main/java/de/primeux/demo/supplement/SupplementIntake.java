package de.primeux.demo.supplement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Whether a single supplement was taken on a given day. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SupplementIntake(String supplement, boolean taken) {
}
