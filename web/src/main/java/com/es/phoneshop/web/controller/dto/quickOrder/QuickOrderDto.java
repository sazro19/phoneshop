package com.es.phoneshop.web.controller.dto.quickOrder;

import java.util.ArrayList;
import java.util.List;

public class QuickOrderDto {

    private List<QuickOrderRow> rows;

    public QuickOrderDto() {
        rows = new ArrayList<>();
    }

    public List<QuickOrderRow> getRows() {
        return rows;
    }

    public void setRows(List<QuickOrderRow> rows) {
        this.rows = rows;
    }
}
