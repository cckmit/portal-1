package ru.protei.portal.core.model.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

/**
 * Траскрипция символов между латинской и кириллической раскладками
 * ЙЦУКЕН - QWERTY
 */
public class AlternativeKeyboardLayoutTextService {
    private static Map<Character, String> rusToLatinCharacters = new HashMap<>();
    private static Map<String, String> charsWithDots = new HashMap<>();

    static Map<Character, Character> latinToCyr = new HashMap<>();
    static Map<Character, Character> cyrToLatin = new HashMap<>();

    static {
        char[] latinCharsUpperCase = {'~', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', '{', '}', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ':', '"', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '<', '>', '?'};
        char[] latinCharsLowerCase = {'`', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', 'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/'};
        char[] cyrilCharsUpperCase = {'Ё', 'Й', 'Ц', 'У', 'К', 'Е', 'Н', 'Г', 'Ш', 'Щ', 'З', 'Х', 'Ъ', 'Ф', 'Ы', 'В', 'А', 'П', 'Р', 'О', 'Л', 'Д', 'Ж', 'Э', 'Я', 'Ч', 'С', 'М', 'И', 'Т', 'Ь', 'Б', 'Ю', ','};
        char[] cyrilCharsLowerCase = {'ё', 'й', 'ц', 'у', 'к', 'е', 'н', 'г', 'ш', 'щ', 'з', 'х', 'ъ', 'ф', 'ы', 'в', 'а', 'п', 'р', 'о', 'л', 'д', 'ж', 'э', 'я', 'ч', 'с', 'м', 'и', 'т', 'ь', 'б', 'ю', '.'};

        for (int i = 0; i < latinCharsLowerCase.length; i++) {
            latinToCyr.put( latinCharsLowerCase[i], cyrilCharsLowerCase[i] );
            latinToCyr.put( latinCharsUpperCase[i], cyrilCharsUpperCase[i] );
            cyrToLatin.put( cyrilCharsLowerCase[i], latinCharsLowerCase[i] );
            cyrToLatin.put( cyrilCharsUpperCase[i], latinCharsUpperCase[i] );
        }
    }

    public static String makeAlternativeSearchString( String searchString ) {
        if (isBlank( searchString )) {
            return null;
        }
        String alternativeString = AlternativeKeyboardLayoutTextService.latinToCyrillic( searchString );
        if (Objects.equals( searchString, alternativeString )) {
            return null;
        }
        return alternativeString;
    }

    public static String makeAlternativeString( String sourceString ) {
        if (isBlank( sourceString )) return sourceString;
        char[] alternative = sourceString.toCharArray();
        char charAt;
        for (int i = 0; i < sourceString.length(); i++) {
            charAt = sourceString.charAt( i );
            if ('.' == charAt) {// неизвестна исходная раскладка
                continue;
            }
            if (',' == charAt) {// неизвестна исходная раскладка
                continue;
            }
            if (latinToCyr.containsKey( charAt )) {
                alternative[i] = latinToCyr.get( charAt );
                continue;
            }
            if (cyrToLatin.containsKey( charAt )) {
                alternative[i] = cyrToLatin.get( charAt );
                continue;
            }
        }

        String alternativeString = new String( alternative );
        return alternativeString;
    }

    public static String latinToCyrillic( String searchString ) {
        if (isBlank( searchString )) return searchString;
        char[] alternative = searchString.toCharArray();
        for (int i = 0; i < searchString.length(); i++) {
            if (latinToCyr.containsKey( searchString.charAt( i ) )) {
                alternative[i] = latinToCyr.get( searchString.charAt( i ) );
                continue;
            }
        }

        String alternativeString = new String( alternative );
        return alternativeString;
    }

    public static String cyrillicToLatin( String cyrillicString ) {
        if (isBlank( cyrillicString )) return cyrillicString;
        char[] alternative = cyrillicString.toCharArray();
        for (int i = 0; i < cyrillicString.length(); i++) {
            if (cyrToLatin.containsKey( cyrillicString.charAt( i ) )) {
                alternative[i] = cyrToLatin.get( cyrillicString.charAt( i ) );
                continue;
            }
        }

        String alternativeString = new String( alternative );
        return alternativeString;
    }
}
