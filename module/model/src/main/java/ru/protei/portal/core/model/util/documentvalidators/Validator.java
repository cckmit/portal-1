package ru.protei.portal.core.model.util.documentvalidators;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

class Validator implements Function<String, Optional<String>> {
    private boolean optional;
    private int valueLength;
    private Predicate<String> isValid;

    public Validator(boolean optional, int valueLength, Predicate<String> isValid) {
        this.optional = optional;
        this.valueLength = valueLength;
        this.isValid = isValid;
    }

    public Validator(int valueLength, Predicate<String> isValid) {
        this(false, valueLength, isValid);
    }

    @Override
    public Optional<String> apply(String s) {
        String temp;
        if (valueLength == 0) {
            temp = s;
        } else {
            if (valueLength <= s.length()) {
                temp = s.substring(0, valueLength);
            }  else {
                return optional ? Optional.of(s) : Optional.empty();
            }
        }
        if (isValid.test(temp)) {
            return Optional.of(s.substring(valueLength));
        }
        return optional ? Optional.of(s) : Optional.empty();
    }
}