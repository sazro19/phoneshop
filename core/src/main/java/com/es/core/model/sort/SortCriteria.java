package com.es.core.model.sort;

public enum SortCriteria {
    BRAND("brand"),
    MODEL("model"),
    DISPLAY_SIZE_INCHES("displaySizeInches"),
    PRICE("price");

    private final String value;

    SortCriteria(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
