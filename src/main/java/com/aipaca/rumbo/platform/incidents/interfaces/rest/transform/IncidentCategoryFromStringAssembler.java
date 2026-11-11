package com.aipaca.rumbo.platform.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentCategory;

import java.util.Locale;

/**
 * Assembler to convert a category text to an IncidentCategory.
 */
public class IncidentCategoryFromStringAssembler {
    /**
     * Converts a category text to an IncidentCategory, regardless of letter case.
     *
     * @param category The category text.
     * @return The {@link IncidentCategory} value.
     * @throws IllegalArgumentException if the category is not supported.
     */
    public static IncidentCategory toCategoryFromString(String category) {
        try {
            return IncidentCategory.valueOf(category.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "category must be MECHANICAL_FAILURE, TRAFFIC_ACCIDENT, ROAD_BLOCKED, STUDENT_HEALTH or OTHER");
        }
    }
}
