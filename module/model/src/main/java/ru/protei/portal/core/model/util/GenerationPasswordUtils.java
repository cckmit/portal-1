package ru.protei.portal.core.model.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GenerationPasswordUtils {
    private static List<Character> cons;
    private static List<Character> vocal;
    private static List<Character> numbers;
    private static final int DEFAULT_PASSWORD_SIZE = 8;
    private static final int DEFAULT_COUNT_OF_NUMBERS = 2;

    static {
        cons = Arrays.asList('b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z');
        vocal = Arrays.asList('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');
        numbers = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    }

    public static String generate() {
        return generate(DEFAULT_PASSWORD_SIZE, DEFAULT_COUNT_OF_NUMBERS);
    }

    public static String generate(int passwordSize, int countOfNumbers) {
        if (passwordSize < countOfNumbers) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < passwordSize - countOfNumbers; i++) {
            if (i % 2 == 0) {
                result.append(cons.get(random.nextInt(cons.size())));
            } else {
                result.append(vocal.get(random.nextInt(vocal.size())));
            }
        }

        for (int i = 0; i < countOfNumbers; i++) {
            result.append(numbers.get(random.nextInt(numbers.size())));
        }

        return result.toString();
    }
}
