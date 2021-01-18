package ru.protei.portal.core.model.helper;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.RUS_PHONE_NUMBER_PATTERN;

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

        String[] split = phoneNumber.split("#");
        if (split.length > 1) {
            return split[0] + " доб. " + prettyExtPhoneNumber(split[1]);
        } else {
            return prettyExtPhoneNumber(phoneNumber);
        }
    }

    public static String prettyExtPhoneNumber(String extPhoneNumber) {
        if (extPhoneNumber.matches(PROTEI_PHONE_NUMBER_PATTERN)) {
            return extPhoneNumber.substring(0, 1) + "-" + (extPhoneNumber.substring(1));
        } else {
            return extPhoneNumber;
        }
    }

    public static String prettyPrintPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        if (!phoneNumber.matches(RUS_PHONE_NUMBER_PATTERN)) {
            return phoneNumber;
        }

        boolean isFullSizedPhoneNumber = phoneNumber.length() > 7;
        int countryCodeLength = isFullSizedPhoneNumber ?
                (phoneNumber.startsWith("+") ? 2 : 1) :
                0;
        int regionCodeLength = isFullSizedPhoneNumber ?
                3 :
                0;
        int numberLength = phoneNumber.length() - regionCodeLength - countryCodeLength;

        String countryCode = phoneNumber.substring(0, countryCodeLength);
        String regionCode = phoneNumber.substring(countryCodeLength, countryCodeLength + regionCodeLength);
        String number = phoneNumber.substring(countryCodeLength + regionCodeLength);
        if (numberLength == 6) {
            number = splitPhoneNumberWithDash(number, 2, 2, 2);
        } else {
            number = splitPhoneNumberWithDash(number, 3, 2, 2);
        }

        return isFullSizedPhoneNumber ?
                countryCode + " (" + regionCode + ") " + number :
                number;
    }

    private static String splitPhoneNumberWithDash(String phoneNumber, int first, int second, int third) {
        return phoneNumber.substring(0, first) +
                "-" +
                phoneNumber.substring(first, first + second) +
                "-" +
                phoneNumber.substring(first + second, first + second + third);
    }

    private static final String PROTEI_PHONE_NUMBER_PATTERN = "^[0-9]{4}$";
    private static final String NOT_ALLOWED_SYMBOLS_REGEX = "[^+#0-9]";
}
