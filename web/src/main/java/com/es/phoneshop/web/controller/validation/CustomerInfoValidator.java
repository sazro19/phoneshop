package com.es.phoneshop.web.controller.validation;

import com.es.phoneshop.web.controller.dto.CustomerInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Service
@PropertySource("classpath:/config/validation.properties")
public class CustomerInfoValidator implements Validator {

    private static final String US_PATTERN = "^(\\([0-9]{3}\\) |[0-9]{3}-)[0-9]{3}-[0-9]{4}$";

    private static final String UK_PATTERN = "^(((\\+44\\s?\\d{4}|\\(?0\\d{4}\\)?)\\s?\\d{3}\\s?\\d{3})|((\\+44\\s?\\d{3}|\\(?0\\d{3}\\)?)\\s?\\d{3}\\s?\\d{4})|((\\+44\\s?\\d{2}|\\(?0\\d{2}\\)?)\\s?\\d{4}\\s?\\d{4}))(\\s?\\#(\\d{4}|\\d{3}))?$";

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
            errors.reject("firstName", environment.getProperty("message.required"));
        }
        if (isStringInvalid(customerInfoDto.getLastName())) {
            errors.reject("lastName", environment.getProperty("message.required"));
        }
        if (isStringInvalid(customerInfoDto.getDeliveryAddress())) {
            errors.reject("deliveryAddress", environment.getProperty("message.required"));
        }
        if (isStringInvalid(customerInfoDto.getContactPhoneNo())) {
            errors.reject("contactPhoneNo", environment.getProperty("message.required"));
        }
        if (isPhoneNumberValid(customerInfoDto.getContactPhoneNo())) {
            String message = environment.getProperty("message.invalidPhone") + ", example: " + phoneNumberExample;
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
        Locale locale = new Locale(environment.getProperty("local.lang"));
        phoneNoPattern = getPattern(locale);
        phoneNumberExample = environment.getProperty("local.format");
    }

    private String getPattern(Locale locale) {
        if (Locale.US.toString().toLowerCase(Locale.ROOT).equals(locale.toString())) {
            return US_PATTERN;
        } else if (Locale.UK.toString().toLowerCase(Locale.ROOT).equals(locale.toString())) {
            return UK_PATTERN;
        }
        throw new IllegalArgumentException();
    }
}
