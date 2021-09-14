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
            Long quantity = quickOrderRow.getQuantity();

            if (isEmptyString(model) && quantity == null) {
                return;
            }

            if (!errors.hasFieldErrors("rows[" + index + "].quantity")) {
                if (isOneFieldEmpty(model, quantity)) {
                    errors.rejectValue("rows[" + index + "].phoneModel", "emptyField", BOTH_FIELDS_ARE_REQUIRED);
                    return;
                }
            }
        });
    }

    private boolean isOneFieldEmpty(String model, Long quantity) {
        return (isEmptyString(model) && quantity != null) ||
                (!isEmptyString(model) && quantity == null);
    }

    private boolean isEmptyString(String string) {
        return string == null || string.trim().isEmpty();
    }
}
