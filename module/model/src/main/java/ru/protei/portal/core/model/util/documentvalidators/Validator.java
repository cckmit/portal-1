package ru.protei.portal.core.model.util.documentvalidators;

import java.util.function.Function;
import java.util.function.Predicate;

class Validator implements Function<ValidationResult, ValidationResult> {
    private Boolean optional;
    private Integer valueLength;
    private Function<String, ValidationResult> validationFunction;

    public Validator(Boolean optional, Function<String, ValidationResult> validationFunction) {
        this.optional = optional;
        this.validationFunction = validationFunction;
        this.valueLength = 0;
    }

    public Validator(Boolean optional, Integer valueLength, Predicate<String> validationFunction) {
        this(optional, (String value) -> new ValidationResult(validationFunction.test(value), valueLength, 0));
        this.valueLength = valueLength;
    }

    public Validator(Function<String, ValidationResult> validationFunction) {
        this(false, validationFunction);
    }

    public Validator(Integer valueLength, Predicate<String> validationFunction) {
        this(false, valueLength, validationFunction);
    }

    @Override
    public ValidationResult apply(ValidationResult s) {
        String validatableStringPart;
        String fullString = s.validatableString;
        if (valueLength == 0) {
            validatableStringPart = fullString;
        } else {
            if (valueLength <= fullString.length()) {
                validatableStringPart = fullString.substring(0, valueLength);
            }  else {
                return optional ? s : new ValidationResult(false);
            }
        }
        ValidationResult result = validationFunction.apply(validatableStringPart);
        if (result.isValid) {
            result.validatableString = fullString.substring(result.processedLength);
            return result;
        }
        if (optional) {
            return new ValidationResult(fullString, 0);
        }
        return new ValidationResult(false);
    }
}