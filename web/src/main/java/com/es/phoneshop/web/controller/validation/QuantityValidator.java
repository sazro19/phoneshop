package com.es.phoneshop.web.controller.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class QuantityValidator implements Validator {
    private static final String QUANTITY_VAR = "quantity";

    private static final String NOT_A_NUMBER_MESSAGE = "Not a number";
    private static final String INVALID_QUANTITY_MESSAGE = "Invalid quantity";

    @Override
    public boolean supports(Class<?> aClass) {
        return QuantityInputWrapper.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        QuantityInputWrapper quantityInputWrapper = ((QuantityInputWrapper) o);
        long quantity;
        try {
            quantity = Long.parseLong(quantityInputWrapper.getQuantity());
        } catch (NumberFormatException e) {
            errors.reject(QUANTITY_VAR, NOT_A_NUMBER_MESSAGE);
            return;
        }

        if (quantity <= 0) {
            errors.reject(QUANTITY_VAR, INVALID_QUANTITY_MESSAGE);
        }
    }
}
