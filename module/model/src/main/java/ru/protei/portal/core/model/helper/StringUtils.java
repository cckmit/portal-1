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
