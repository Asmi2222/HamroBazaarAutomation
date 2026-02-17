package com.hamrobazaar.enums;

/**
 * SortOrder - Enum for all available sort options on HamroBazaar
 * Maps CSV sort order values to their display text in the dropdown
 */
public enum SortOrder {

    LOW_TO_HIGH ("Low to High (Price)"),
    HIGH_TO_LOW ("High to Low (Price)"),
    A_TO_Z      ("A to Z"),
    RECENT      ("Recent");

    private final String displayText;

    SortOrder(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    /**
     * Convert a CSV string value to the matching SortOrder enum
     * Case-insensitive and trims whitespace
     */
    public static SortOrder fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return RECENT;
        }
        for (SortOrder sort : values()) {
            if (sort.displayText.equalsIgnoreCase(value.trim())) {
                return sort;
            }
        }
        throw new IllegalArgumentException("No SortOrder found for value: '" + value + "'");
    }
}