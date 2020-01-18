package ru.protei.portal.core.model.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerationPasswordUtils {
    private static List<String> symbols = new ArrayList<>();

    static {
        Character symbol;

//        [A-Z]
        for (int i = 65; i < 91; i++) {
            symbol = (char) i;
            symbols.add(symbol.toString());
        }

//        [a-z]
        for (int i = 97; i < 123; i++) {
            symbol = (char) i;
            symbols.add(symbol.toString());
        }

//        [0-9]
        for (int i = 48; i < 58; i++) {
            symbol = (char) i;
            symbols.add(symbol.toString());
        }
    }

    public static String generate(int countOfSymbols) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < countOfSymbols; i++) {
            result.append(symbols.get(random.nextInt(symbols.size())));
        }

        return result.toString();
    }
}
