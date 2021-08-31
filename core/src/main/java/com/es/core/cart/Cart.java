package com.es.core.cart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class Cart implements Serializable {
    private List<CartItem> itemList;

    private long totalQuantity;

    private BigDecimal totalCost;

    private final Currency currency = Currency.getInstance(Locale.US);

    public Cart() {
        this.itemList = new ArrayList<>();
        this.totalQuantity = 0L;
        this.totalCost = new BigDecimal(0);
    }

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

    public Currency getCurrency() {
        return currency;
    }

    public List<CartItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<CartItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return itemList.toString();
    }
}
