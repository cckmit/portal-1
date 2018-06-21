package ru.protei.portal.core.model.helper;

public class StringUtils {

    public static boolean isEmpty( String string ) {
        return null == string || string.isEmpty();
    }

    public static boolean isBlank( String string ) {
        return null == string || string.trim().isEmpty();
    }

    public static String defaultString( String value, String defaultString ) {
        return value != null ? value : defaultString;
    }

}
