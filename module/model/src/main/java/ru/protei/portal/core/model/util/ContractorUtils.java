package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.helper.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContractorUtils {
    public static final List<Integer> MULT_N1 = Arrays.asList(7, 2, 4, 10, 3, 5, 9, 4, 6, 8);
    public static final List<Integer> MULT_N2 = Arrays.asList(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8);
    public static final List<Integer> MULT_N =  Arrays.asList(2, 4, 10, 3, 5, 9, 4, 6, 8);

    public static boolean checkInn(String innStr) {
        if (innStr == null) {
            return false;
        }

        boolean valid;
        List<Integer> inn = stringToIntArray(innStr);

        switch (inn.size()) {
            case 12:
                Integer N1 = getChecksum(inn,MULT_N1);
                Integer N2 = getChecksum(inn,MULT_N2);

                valid = (inn.get(inn.size()-1).equals(N2) && inn.get(inn.size()-2).equals(N1));
                break;
            case 10:
                Integer N = getChecksum(inn,MULT_N);
                valid = (inn.get(inn.size()-1).equals(N));
                break;
            default:
                valid = false;
                break;
        }
        return valid;
    }

    private static List<Integer> stringToIntArray(String src) {
        List<Integer> digits = new ArrayList<>();
        for (int i1 = 0, i2 = 1 ; i2 <= src.length(); i1++, i2++) {
            digits.add(NumberUtils.parseInteger(src.substring(i1, i2)));
        }
        return digits;
    }

    private static Integer getChecksum(List<Integer> digits, List<Integer> multipliers) {
        int checksum = 0;
        for (int i = 0; i < multipliers.size(); i++) {
            checksum += (digits.get(i) * multipliers.get(i));
        }
        return (checksum % 11) % 10;
    }
}
