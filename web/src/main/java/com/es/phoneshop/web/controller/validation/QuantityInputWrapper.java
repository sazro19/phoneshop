package com.es.phoneshop.web.controller.validation;

public class QuantityInputWrapper {
    private String quantity;

    public QuantityInputWrapper() {
    }

    public QuantityInputWrapper(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
