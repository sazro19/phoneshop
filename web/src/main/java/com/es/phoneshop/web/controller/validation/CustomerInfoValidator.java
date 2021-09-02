package com.es.phoneshop.web.controller.validation;

import com.es.core.model.phoneNoPatterns.Local;
import com.es.core.model.phoneNoPatterns.PhoneNoPatterns;
import com.es.phoneshop.web.controller.dto.CustomerInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
@PropertySource("classpath:/config/local.properties")
public class CustomerInfoValidator implements Validator {

    private static final String FIELD_IS_REQUIRED_MESSAGE = "Field is required";

    private static final String INVALID_PHONE_NUMBER_FORMAT_MESSAGE = "Invalid phone number";

    private String phoneNumberExample;

    private String phoneNoPattern;

    @Autowired
    private Environment environment;

    @Override
    public boolean supports(Class<?> aClass) {
        return CustomerInfoDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CustomerInfoDto customerInfoDto = ((CustomerInfoDto) o);
        setPhoneNoPatternAndExample();

        if (isStringInvalid(customerInfoDto.getFirstName())) {
            errors.reject("firstName", FIELD_IS_REQUIRED_MESSAGE);
        }
        if (isStringInvalid(customerInfoDto.getLastName())) {
            errors.reject("lastName", FIELD_IS_REQUIRED_MESSAGE);
        }
        if (isStringInvalid(customerInfoDto.getDeliveryAddress())) {
            errors.reject("deliveryAddress", FIELD_IS_REQUIRED_MESSAGE);
        }
        if (isStringInvalid(customerInfoDto.getContactPhoneNo())) {
            errors.reject("contactPhoneNo", FIELD_IS_REQUIRED_MESSAGE);
        }
        if (isPhoneNumberValid(customerInfoDto.getContactPhoneNo())) {
            String message = INVALID_PHONE_NUMBER_FORMAT_MESSAGE + ", example: " + phoneNumberExample;
            errors.reject("contactPhoneNo", message);
        }
    }

    private boolean isStringInvalid(String string) {
        return string == null || string.replaceAll("[\\s]{2,}", "").isEmpty();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return !phoneNumber.matches(phoneNoPattern);
    }

    private void setPhoneNoPatternAndExample() {
        Local local = Local.valueOf(environment.getProperty("local.lang"));
        phoneNoPattern = PhoneNoPatterns.getPattern(local);
        phoneNumberExample = environment.getProperty("local.format");
    }
}
