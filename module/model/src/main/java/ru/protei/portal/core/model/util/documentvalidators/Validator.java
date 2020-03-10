package ru.protei.portal.core.model.util.documentvalidators;

import java.util.function.Function;

class Validator implements Function<ValidationResult, ValidationResult> {
    protected Boolean optional;
    protected Function<String, ValidationResult> validationFunction;

    public Validator(Boolean optional, Function<String, ValidationResult> validationFunction) {
        this.optional = optional;
        this.validationFunction = validationFunction;
    }

    public Validator(Function<String, ValidationResult> validationFunction) {
        this.optional = false;
        this.validationFunction = validationFunction;
    }

    @Override
    public ValidationResult apply(ValidationResult s) {
        ValidationResult result = validationFunction.apply(s.validatableString);
        if (result.isValid) {
            return result;
        }
        if (optional) {
            return s;
        } else {
            return new ValidationResult(false);
        }
    }
}