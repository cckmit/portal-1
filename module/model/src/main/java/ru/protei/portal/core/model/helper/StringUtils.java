package ru.protei.portal.core.model.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StringUtils {

    /**
     * Checks if a String is empty ("") or null.
     */
    public static boolean isEmpty( String string ) {
        return null == string || string.isEmpty();
    }

    public static boolean isEmpty( CharSequence cs ) {
        return null == cs || cs.length() < 1;
    }

    /**
     * Checks if a String is whitespace, empty ("") or null.
     */
    public static boolean isBlank( String string ) {
        return null == string || string.trim().isEmpty();
    }

    /**
     * Checks if a String is not empty ("") and not null.
     */
    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
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

    /**
     * null - не выводится
     */
    public static StringBuilder join(CharSequence... str) {
        return join(null, str);
    }

    /**
     * null - не выводится
     */
    public static StringBuilder join(StringBuilder sb, CharSequence... str) {
        if (sb == null) sb = new StringBuilder();
        for (CharSequence chars : str) {
            if (chars == null) continue;
            sb.append(chars);
        }
        return sb;
    }

    /**
     * null - не выводится
     */
    public static <T> String join( Iterable<T> iterable, Function<T, String> mapper, CharSequence delimiter) {
        if (iterable == null) {
            return null;
        }

        Iterator<T> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return "";
        }

        if (mapper == null) {
            mapper = String::valueOf;
        }

        T first = iterator.next();
        if (!iterator.hasNext()) {
            return first == null ? "" : mapper.apply( first );
        }

        StringBuilder buf = new StringBuilder( 256 ); // Java default is 16, probably too small
        if (first != null) {
            buf.append( mapper.apply( first ) );
        }

        while (iterator.hasNext()) {
            T obj = iterator.next();
            if (obj != null) {
                if(buf.length() > 0) {
                    buf.append( delimiter );
                }
                buf.append( mapper.apply( obj ) );
            }
        }

        return buf.toString();
    }

    public static String join(Collection<?> collection, CharSequence delimiter) {
        return join(collection, Object::toString, delimiter);
    }

    public static <T> String join(Iterable<T> iterable, BiConsumer<T, StringBuilder> consumer) {
        if (iterable == null || consumer == null)
            return "";

        StringBuilder sb = new StringBuilder();
        Iterator<T> it = iterable.iterator();
        while(it.hasNext()){
            consumer.accept( it.next(), sb );
        }

        return sb.toString();
    }

    public static String trim( String string ) {
        return null == string ? null : string.trim();
    }

    public static int length(CharSequence string) {
        return null == string ? 0 : string.length();
    }

    public static String firstUppercaseChar(String string) {
        return null == string ? null : string.substring(0, 1).toUpperCase();
    }
}
