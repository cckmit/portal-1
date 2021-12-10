package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.helper.StringUtils;

import java.util.*;

public class TransliterationUtils {
    private static Map<Character, String> rusToLatinCharacters = new HashMap<>();
    private static Map<String, String> charsWithDots = new HashMap<>();

    static {
        charsWithDots.put("Е.", "E.");
        charsWithDots.put("Ё.", "E.");
        charsWithDots.put("Ч.", "C.");
        charsWithDots.put("Ш.", "S.");
        charsWithDots.put("Щ.", "S.");
        charsWithDots.put("Ю.", "J.");
        charsWithDots.put("Я.", "J.");

        rusToLatinCharacters.put('а', "a");
        rusToLatinCharacters.put('б', "b");
        rusToLatinCharacters.put('в', "v");
        rusToLatinCharacters.put('г', "g");
        rusToLatinCharacters.put('д', "d");
        rusToLatinCharacters.put('е', "e");
        rusToLatinCharacters.put('ё', "e");
        rusToLatinCharacters.put('ж', "zh");
        rusToLatinCharacters.put('з', "z");
        rusToLatinCharacters.put('и', "i");
        rusToLatinCharacters.put('й', "j");
        rusToLatinCharacters.put('к', "k");
        rusToLatinCharacters.put('л', "l");
        rusToLatinCharacters.put('м', "m");
        rusToLatinCharacters.put('н', "n");
        rusToLatinCharacters.put('о', "o");
        rusToLatinCharacters.put('п', "p");
        rusToLatinCharacters.put('р', "r");
        rusToLatinCharacters.put('с', "s");
        rusToLatinCharacters.put('т', "t");
        rusToLatinCharacters.put('у', "u");
        rusToLatinCharacters.put('ф', "f");
        rusToLatinCharacters.put('х', "kh");
        rusToLatinCharacters.put('ц', "c");
        rusToLatinCharacters.put('ч', "ch");
        rusToLatinCharacters.put('ш', "sh");
        rusToLatinCharacters.put('щ', "shch");
        rusToLatinCharacters.put('ъ', "ʺ");
        rusToLatinCharacters.put('ы', "y");
        rusToLatinCharacters.put('ь', "'");
        rusToLatinCharacters.put('э', "e");
        rusToLatinCharacters.put('ю', "ju");
        rusToLatinCharacters.put('я', "ya");

        rusToLatinCharacters.put('А', "A");
        rusToLatinCharacters.put('Б', "B");
        rusToLatinCharacters.put('В', "V");
        rusToLatinCharacters.put('Г', "G");
        rusToLatinCharacters.put('Д', "D");
        rusToLatinCharacters.put('Е', "E");
        rusToLatinCharacters.put('Ё', "E");
        rusToLatinCharacters.put('Ж', "Zh");
        rusToLatinCharacters.put('З', "Z");
        rusToLatinCharacters.put('И', "I");
        rusToLatinCharacters.put('Й', "J");
        rusToLatinCharacters.put('К', "K");
        rusToLatinCharacters.put('Л', "L");
        rusToLatinCharacters.put('М', "M");
        rusToLatinCharacters.put('Н', "N");
        rusToLatinCharacters.put('О', "O");
        rusToLatinCharacters.put('П', "P");
        rusToLatinCharacters.put('Р', "R");
        rusToLatinCharacters.put('С', "S");
        rusToLatinCharacters.put('Т', "T");
        rusToLatinCharacters.put('У', "U");
        rusToLatinCharacters.put('Ф', "F");
        rusToLatinCharacters.put('Х', "Kh");
        rusToLatinCharacters.put('Ц', "C");
        rusToLatinCharacters.put('Ч', "Ch");
        rusToLatinCharacters.put('Ш', "Sh");
        rusToLatinCharacters.put('Щ', "Shch");
        rusToLatinCharacters.put('Ъ', "ʺ");
        rusToLatinCharacters.put('Ы', "Y");
        rusToLatinCharacters.put('Ь', "'");
        rusToLatinCharacters.put('Э', "E");
        rusToLatinCharacters.put('Ю', "Ju");
        rusToLatinCharacters.put('Я', "Ya");
    }

    public static String transliterate(String input) {
        return transliterate(input, CrmConstants.LocaleTags.EN);
    }

    public static String transliterate(String input, String localeTag) {
        if (StringUtils.isBlank(input)) {
            return "";
        }

        if (Objects.equals(localeTag, CrmConstants.LocaleTags.RU)) {
            return input;
        }

        StringBuilder stringBuilder = new StringBuilder();

        input = charsWithDotsReplace(input);

        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            stringBuilder.append(transliterateChar(chars[i]));
        }

        return stringBuilder.toString();
    }

    private static String transliterateChar(Character currChar) {
        if (rusToLatinCharacters.containsKey(currChar)) {
            return rusToLatinCharacters.get(currChar);
        } else {
            return String.valueOf(currChar);
        }
    }

    private static String charsWithDotsReplace(String input) {
        String result = input;

        for (Map.Entry<String, String> currMap : charsWithDots.entrySet()) {
            result = result.replace(currMap.getKey(), currMap.getValue());
        }

        return result;
    }
}
