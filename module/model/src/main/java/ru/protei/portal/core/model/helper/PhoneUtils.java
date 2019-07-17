package ru.protei.portal.core.model.helper;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class PhoneUtils {

    public static String normalizePhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        return phoneNumber.replaceAll(NOT_ALLOWED_SYMBOLS_REGEX, "");
    }

    public static String prettyPrintWorkPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        if (!phoneNumber.matches(PROTEI_PHONE_NUMBER_PATTERN)) {
            return phoneNumber;
        }

        return phoneNumber.substring(0, 1) + "-" + phoneNumber.substring(1);
    }

    public static String prettyPrintPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        if (!phoneNumber.matches(RUS_PHONE_NUMBER_PATTERN)) {
            return phoneNumber;
        }

        int countryCodeLength = phoneNumber.startsWith("+") ? 2 : 1;
        int regionCodeLength = 3;
        int numberLength = phoneNumber.length() - regionCodeLength - countryCodeLength;

        String countryCode = phoneNumber.substring(0, countryCodeLength);
        String regionCode = phoneNumber.substring(countryCodeLength, countryCodeLength + regionCodeLength);
        String number = phoneNumber.substring(countryCodeLength + regionCodeLength);
        if (numberLength == 6) {
            number = splitPhoneNumberWithDash(number, 2, 2, 2);
        } else {
            number = splitPhoneNumberWithDash(number, 3, 2, 2);
        }

        return countryCode + " (" + regionCode + ") " + number;
    }

    private static String splitPhoneNumberWithDash(String phoneNumber, int first, int second, int third) {
        return phoneNumber.substring(0, first) +
                "-" +
                phoneNumber.substring(first, first + second) +
                "-" +
                phoneNumber.substring(first + second, first + second + third);
    }

    private static final String PROTEI_PHONE_NUMBER_PATTERN = "^[0-9]{4}$";
    private static final String RUS_PHONE_NUMBER_PATTERN = "^(\\+7|8)[0-9]{9,10}$"; // [+7 или 8] + [3 код региона] + [6-7 номер]
    private static final String NOT_ALLOWED_SYMBOLS_REGEX = "[^+0-9]";
}
