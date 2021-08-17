package com.es.phoneshop.web.controller.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class MiniCartDTO implements Serializable {
    private long totalQuantity;

    private BigDecimal totalCost;

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
