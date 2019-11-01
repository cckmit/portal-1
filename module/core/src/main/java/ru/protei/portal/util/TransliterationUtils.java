package ru.protei.portal.util;

import com.ibm.icu.text.Transliterator;

import java.util.HashMap;
import java.util.Map;

public class TransliterationUtils {
    public static void main(String[] args) {
        Transliterator toLatin = Transliterator.getInstance("Russian-Latin/BGN");
        System.out.println(
                toLatin.transliterate("а б в г д е ё ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я")
        );

//        a b v g d ye yë zh z i y k l m n o p r s t u f kh ts ch sh shch ʺ y ʹ e yu ya

        Map<String, String> rusToLatin = new HashMap<>();
        rusToLatin.put("а", "a");
        rusToLatin.put("б", "b");
        rusToLatin.put("в", "v");
        rusToLatin.put("г", "g");
        rusToLatin.put("д", "d");
        rusToLatin.put("е", "ye");
        rusToLatin.put("ё", "yo");
        rusToLatin.put("ж", "zh");
        rusToLatin.put("з", "z");
        rusToLatin.put("и", "i");
        rusToLatin.put("й", "y");
        rusToLatin.put("к", "k");
        rusToLatin.put("л", "l");
        rusToLatin.put("м", "m");
        rusToLatin.put("н", "n");
        rusToLatin.put("о", "o");
        rusToLatin.put("п", "p");
        rusToLatin.put("р", "r");
        rusToLatin.put("с", "s");
        rusToLatin.put("т", "t");
        rusToLatin.put("у", "u");
        rusToLatin.put("ф", "f");
        rusToLatin.put("х", "kh");
        rusToLatin.put("ц", "ts");
        rusToLatin.put("ч", "ch");
        rusToLatin.put("ш", "sh");
        rusToLatin.put("щ", "shch");
        rusToLatin.put("ъ", "ʺ");
        rusToLatin.put("ы", "a");
        rusToLatin.put("ь", "a");
        rusToLatin.put("э", "a");
        rusToLatin.put("ю", "a");
        rusToLatin.put("я", "a");

        System.out.println(rusToLatin.get("а"));
    }
}
