package ru.protei.portal.core.model.helper;

public class StringUtils {

    /**
     * Checks if a String is empty ("") or null.
     */
    public static boolean isEmpty( String string ) {
        return null == string || string.isEmpty();
    }

    /**
     * Checks if a String is whitespace, empty ("") or null.
     */
    public static boolean isBlank( String string ) {
        return null == string || string.trim().isEmpty();
    }

    /**
     * Checks if a String is not empty (""), not null and not whitespace only.
     */
    public static boolean isNotBlank( String string ) {
        return !isBlank(string);
    }

    /**
     * Returns either the passed in String, or if the String is null, the value of defaultString.
     */
    public static String defaultString( String value, String defaultString ) {
        return value != null ? value : defaultString;
    }

    public static int length(String string) {
        return null == string ? 0 : string.length();
    }

    public static String trim( String string ) {
        return null == string ? null : string.trim();
    }

}
