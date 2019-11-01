package ru.protei.portal.util;

import com.ibm.icu.text.Transliterator;

import java.util.HashMap;
import java.util.Map;

public class TransliterationUtils {
    public static void main(String[] args) {
        String msg = "приехать ко мне";
//        Transliterator toLatin = Transliterator.getInstance("Russian-Latin/BGN");
//        System.out.println(
//                toLatin.transliterate(toLatin.transliterate(msg))
//        );

//        a b v g d ye yë zh z i y k l m n o p r s t u f kh ts ch sh shch ʺ y ʹ e yu ya

//        Map<Character, String> rusToLatin = new HashMap<>();
//
//        rusToLatin.put('а', "a");
//        rusToLatin.put('б', "b");
//        rusToLatin.put('в', "v");
//        rusToLatin.put('г', "g");
//        rusToLatin.put('д', "d");
//        rusToLatin.put('е', "ye");
//        rusToLatin.put('ё', "yo");
//        rusToLatin.put('ж', "zh");
//        rusToLatin.put('з', "z");
//        rusToLatin.put('и', "i");
//        rusToLatin.put('й', "y");
//        rusToLatin.put('к', "k");
//        rusToLatin.put('л', "l");
//        rusToLatin.put('м', "m");
//        rusToLatin.put('н', "n");
//        rusToLatin.put('о', "o");
//        rusToLatin.put('п', "p");
//        rusToLatin.put('р', "r");
//        rusToLatin.put('с', "s");
//        rusToLatin.put('т', "t");
//        rusToLatin.put('у', "u");
//        rusToLatin.put('ф', "f");
//        rusToLatin.put('х', "kh");
//        rusToLatin.put('ц', "ts");
//        rusToLatin.put('ч', "ch");
//        rusToLatin.put('ш', "sh");
//        rusToLatin.put('щ', "shch");
//        rusToLatin.put('ъ', "ʺ");
//        rusToLatin.put('ы', "y");
//        rusToLatin.put('ь', "ʹ");
//        rusToLatin.put('э', "e");
//        rusToLatin.put('ю', "yu");
//        rusToLatin.put('я', "ya");
//
//        rusToLatin.put('А', "A");
//        rusToLatin.put('Б', "B");
//        rusToLatin.put('В', "V");
//        rusToLatin.put('Г', "G");
//        rusToLatin.put('Д', "D");
//        rusToLatin.put('Е', "Ye");
//        rusToLatin.put('Ё', "Yo");
//        rusToLatin.put('Ж', "Zh");
//        rusToLatin.put('З', "Z");
//        rusToLatin.put('И', "I");
//        rusToLatin.put('Й', "Y");
//        rusToLatin.put('К', "K");
//        rusToLatin.put('Л', "L");
//        rusToLatin.put('М', "M");
//        rusToLatin.put('Н', "N");
//        rusToLatin.put('О', "O");
//        rusToLatin.put('П', "P");
//        rusToLatin.put('Р', "R");
//        rusToLatin.put('С', "S");
//        rusToLatin.put('Т', "T");
//        rusToLatin.put('У', "U");
//        rusToLatin.put('Ф', "F");
//        rusToLatin.put('Х', "Kh");
//        rusToLatin.put('Ц', "Ts");
//        rusToLatin.put('Ч', "Ch");
//        rusToLatin.put('Ш', "Sh");
//        rusToLatin.put('Щ', "Shch");
//        rusToLatin.put('Ъ', "ʺ");
//        rusToLatin.put('Ы', "Y");
//        rusToLatin.put('Ь', "ʹ");
//        rusToLatin.put('Э', "E");
//        rusToLatin.put('Ю', "Yu");
//        rusToLatin.put('Я', "Ya");

        System.out.println("Привет мир");
    }
    
    private static String toLatin(String input, Map<Character, String> library) {
        StringBuilder stringBuilder = new StringBuilder();

        char[] chars = input.toCharArray();
        for (char currChar : chars) {
            String letter = library.get(currChar);
            stringBuilder.append(letter == null ? currChar : letter);
        }

        return stringBuilder.toString();
    }
}
