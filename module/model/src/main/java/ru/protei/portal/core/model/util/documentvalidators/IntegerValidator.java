package ru.protei.portal.core.model.util.documentvalidators;

import java.util.function.Predicate;

class IntegerValidator extends Validator {
    public IntegerValidator(int valueLength, Predicate<Integer> isValid) {
        super(valueLength, (String s) -> {
            try {
                Integer number = Integer.parseInt(s);
                return isValid.test(number);
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }
    public IntegerValidator(Boolean optional, int valueLength, Predicate<Integer> isValid) {
        super(optional, valueLength, (String s) -> {
            try {
                Integer number = Integer.parseInt(s);
                return isValid.test(number);
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }
}