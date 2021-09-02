package com.es.core.model.phoneNoPatterns;

public class PhoneNoPatterns {

    private static final String US_PATTERN = "^(\\(\\d{3}\\))([[:blank:]])\\d{3}-\\d{4}$|^\\d{3}(-)\\d{3}(-)\\d{4}$|^\\d{10}$";

    private static final String BY_PATTERN = "^[+]375[(](17|29|33|44)[)]*[\\\\s./0-9]{7}$";

    public static String getPattern(Local local) {
        switch (local) {
            case US:
                return US_PATTERN;
            case BY:
                return BY_PATTERN;
            default:
                throw new IllegalArgumentException();
        }
    }
}
