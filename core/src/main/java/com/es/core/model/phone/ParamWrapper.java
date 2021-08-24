package com.es.core.model.phone;

import com.es.core.model.sort.SortCriteria;
import com.es.core.model.sort.SortOrder;

public class ParamWrapper {
    private final String query;

    private final SortCriteria sortCriteria;

    private final SortOrder sortOrder;

    private final int offset;

    private final int limit;

    public ParamWrapper(String query, SortCriteria sortCriteria, SortOrder sortOrder, int offset, int limit) {
        this.query = query;
        this.sortCriteria = sortCriteria;
        this.sortOrder = sortOrder;
        this.offset = offset;
        this.limit = limit;
    }

    public String getQuery() {
        return query;
    }

    public SortCriteria getSortCriteria() {
        return sortCriteria;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
