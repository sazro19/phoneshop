package com.es.phoneshop.web.controller.validation;

import com.es.phoneshop.web.controller.dto.quickOrder.QuickOrderDto;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class QuickOrderValidator implements Validator {

    private static final String NOT_A_NUMBER_MESSAGE = "Not a number";
    private static final String BOTH_FIELDS_ARE_REQUIRED = "Both fields are required";

    @Override
    public boolean supports(Class<?> aClass) {
        return QuickOrderDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        QuickOrderDto quickOrderDto = (QuickOrderDto) o;

        quickOrderDto.getRows().forEach(quickOrderRow -> {
            Long index = quickOrderRow.getRowId();
            String model = quickOrderRow.getPhoneModel();
            String quantity = quickOrderRow.getQuantity();

            if (isEmptyString(model) && isEmptyString(quantity)) {
                return;
            }

            if (isOneFieldEmpty(model, quantity)) {
                errors.rejectValue("rows[" + index + "].phoneModel", "emptyField", BOTH_FIELDS_ARE_REQUIRED);
                return;
            }

            try {
                Long.parseLong(quantity);
            } catch (NumberFormatException e) {
                errors.rejectValue("rows[" + index + "].quantity", "notNumber", NOT_A_NUMBER_MESSAGE);
            }
        });
    }

    private boolean isStringInvalid(String string) {
        return string == null || string.trim().isEmpty();
    }

    private boolean isOneFieldEmpty(String model, String quantity) {
        return (isEmptyString(model) && !isEmptyString(quantity)) ||
                (!isEmptyString(model) && isEmptyString(quantity));
    }

    private boolean isEmptyString(String string) {
        return string == null || string.trim().isEmpty();
    }
}
