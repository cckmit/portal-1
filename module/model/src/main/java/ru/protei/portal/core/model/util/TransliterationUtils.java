package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.helper.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TransliterationUtils {
    static Map<Character, String> rusToLatinCharacters = new HashMap<>();
    static Map<Character, String> hardNoise = new HashMap<>();
    static List<Character> vowelsAndSigns = new LinkedList<>();
    static Map<String, String> hardNoiseWithDots = new HashMap<>();

    static {
        vowelsAndSigns.add('a');
        vowelsAndSigns.add('е');
        vowelsAndSigns.add('ё');
        vowelsAndSigns.add('и');
        vowelsAndSigns.add('о');
        vowelsAndSigns.add('у');
        vowelsAndSigns.add('ъ');
        vowelsAndSigns.add('ы');
        vowelsAndSigns.add('ь');
        vowelsAndSigns.add('э');
        vowelsAndSigns.add('ю');
        vowelsAndSigns.add('я');

        vowelsAndSigns.add('А');
        vowelsAndSigns.add('Е');
        vowelsAndSigns.add('Ё');
        vowelsAndSigns.add('И');
        vowelsAndSigns.add('О');
        vowelsAndSigns.add('У');
        vowelsAndSigns.add('Ъ');
        vowelsAndSigns.add('Ы');
        vowelsAndSigns.add('Ь');
        vowelsAndSigns.add('Э');
        vowelsAndSigns.add('Ю');
        vowelsAndSigns.add('Я');

        hardNoise.put('е', "ye");
        hardNoise.put('ё', "yo");
        hardNoise.put('ю', "yu");
        hardNoise.put('я', "ya");

        hardNoise.put('Е', "Ye");
        hardNoise.put('Ё', "Yo");
        hardNoise.put('Ю', "Yu");
        hardNoise.put('Я', "Ya");

        hardNoiseWithDots.put("Е.", "E.");
        hardNoiseWithDots.put("Ё.", "E.");
        hardNoiseWithDots.put("Ю.", "Y.");
        hardNoiseWithDots.put("Я.", "Y.");

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
        rusToLatinCharacters.put('й', "y");
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
        rusToLatinCharacters.put('ц', "ts");
        rusToLatinCharacters.put('ч', "ch");
        rusToLatinCharacters.put('ш', "sh");
        rusToLatinCharacters.put('щ', "shch");
        rusToLatinCharacters.put('ъ', "ʺ");
        rusToLatinCharacters.put('ы', "y");
        rusToLatinCharacters.put('ь', "");
        rusToLatinCharacters.put('э', "e");
        rusToLatinCharacters.put('ю', "u");
        rusToLatinCharacters.put('я', "ia");

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
        rusToLatinCharacters.put('Й', "Y");
        rusToLatinCharacters.put('К', "K");
        rusToLatinCharacters.put('Л', "L");
        rusToLatinCharacters.put('М', "M");
        rusToLatinCharacters.put('Н', "N");
        rusToLatinCharacters.put('О', "Yo");
        rusToLatinCharacters.put('П', "P");
        rusToLatinCharacters.put('Р', "R");
        rusToLatinCharacters.put('С', "S");
        rusToLatinCharacters.put('Т', "T");
        rusToLatinCharacters.put('У', "U");
        rusToLatinCharacters.put('Ф', "F");
        rusToLatinCharacters.put('Х', "Kh");
        rusToLatinCharacters.put('Ц', "Ts");
        rusToLatinCharacters.put('Ч', "Ch");
        rusToLatinCharacters.put('Ш', "Sh");
        rusToLatinCharacters.put('Щ', "Shch");
        rusToLatinCharacters.put('Ъ', "ʺ");
        rusToLatinCharacters.put('Ы', "Y");
        rusToLatinCharacters.put('Ь', "");
        rusToLatinCharacters.put('Э', "E");
        rusToLatinCharacters.put('Ю', "Yu");
        rusToLatinCharacters.put('Я', "Ia");
    }

    public static String rusToLatin(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        input = staticReplaces(input);

        char[] chars = input.toCharArray();

        stringBuilder.append(transliterate(null, chars[0]));
        for (int i = 1; i < chars.length; i++) {
            stringBuilder.append(transliterate(chars[i - 1], chars[i]));
        }

        return stringBuilder.toString();
    }

    private static String transliterate(Character prevChar, Character currChar) {
        if (hardNoise.containsKey(currChar) && (prevChar == null || vowelsAndSigns.contains(prevChar))) {
            return hardNoise.get(currChar);
        } else if (rusToLatinCharacters.containsKey(currChar)) {
            return rusToLatinCharacters.get(currChar);
        } else {
            return String.valueOf(currChar);
        }
    }

    private static String staticReplaces(String input) {
        String result = input;
        result = result.replace("НТЦ", "NTC");
        result = result.replace("Протей", "Protei");

        for (Map.Entry<String, String> currMap : hardNoiseWithDots.entrySet()) {
            result = result.replace(currMap.getKey(), currMap.getValue());
        }

        return result;
    }

//      for tests
//    public static void main(String[] args) {
//        System.out.println(rusToLatin(""));
//    }
}
