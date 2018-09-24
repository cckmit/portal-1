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


    public static String emptyIfNull(String s) {
        return s == null ? "" : s;
    }

    public static String nullIfEmpty(String s) {
        return isEmpty(s) ? null : s;
    }

    public static StringBuilder join(CharSequence... str) {
        return join(null, str);
    }

    public static StringBuilder join(StringBuilder sb, CharSequence... str) {
        if (sb == null) sb = new StringBuilder();
        for (CharSequence chars : str) {
            sb.append(chars);
        }
        return sb;
    }
}
