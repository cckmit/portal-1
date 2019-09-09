package ru.protei.portal.core.model.helper;

/**
 *
 */
public class NumberUtils {

    public static Integer parseInteger( String value ) {
        return parseInteger(value, null);
    }

    public static Integer parseInteger( String value, Integer defaultValue ) {
        try {
            return Integer.parseInt( value );
        } catch (NumberFormatException ignore) {
            return defaultValue;
        }
    }

    public static Long parseLong( String value ) {
        return parseLong(value, null);
    }

    public static Long parseLong( String value, Long defaultValue ) {
        try {
            return Long.parseLong( value );
        } catch (NumberFormatException ignore) {
            return defaultValue;
        }
    }

}
