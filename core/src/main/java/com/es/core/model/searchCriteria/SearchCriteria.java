package com.es.core.model.searchCriteria;

public enum SearchCriteria {
    MODEL("Model"),
    DESCRIPTION("Description");

    private final String value;

    SearchCriteria(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
