package ru.protei.portal.core.model.util.documentvalidators;

import java.util.function.Function;
import java.util.function.Predicate;

import static ru.protei.portal.core.model.util.documentvalidators.DocumentDecimalNumberValidator.ValidationResult;

class Validator implements Function<ValidationResult, ValidationResult> {
    private boolean optional;
    private int valueLength;
    private Function<String, ValidationResult> validationFunction;

    public Validator(boolean optional, Function<String, ValidationResult> validationFunction) {
        this.optional = optional;
        this.validationFunction = validationFunction;
        this.valueLength = 0;
    }

    public Validator(boolean optional, int valueLength, Predicate<String> validationFunction) {
        this(optional, (String value) -> new ValidationResult(validationFunction.test(value), valueLength, 0));
        this.valueLength = valueLength;
    }

    public Validator(Function<String, ValidationResult> validationFunction) {
        this(false, validationFunction);
    }

    public Validator(int valueLength, Predicate<String> validationFunction) {
        this(false, valueLength, validationFunction);
    }

    @Override
    public ValidationResult apply(ValidationResult s) {
        String temp;
        String value = s.value;
        if (valueLength == 0) {
            temp = value;
        } else {
            if (valueLength <= value.length()) {
                temp = value.substring(0, valueLength);
            }  else {
                return optional ? s : new ValidationResult();
            }
        }
        ValidationResult result = validationFunction.apply(temp);
        if (result.isValid) {
            result.value = value.substring(result.processedLength);
            return result;
        }
        if (optional) {
            result.value = value;
            result.isValid = true;
            result.processedLength = 0;
            return result;
        }
        return new ValidationResult();
    }
}