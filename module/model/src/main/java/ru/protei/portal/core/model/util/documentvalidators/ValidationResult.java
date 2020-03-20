package ru.protei.portal.core.model.util.documentvalidators;

import java.util.Objects;
import java.util.function.Function;

class ValidationResult {
    Boolean isValid;
    String validatableString;

    public ValidationResult(Boolean isValid, String validatableString) {
        this.isValid = isValid;
        this.validatableString = validatableString;
    }

    public ValidationResult(String validatableString) {
        this.isValid = true;
        this.validatableString = validatableString;
    }

    public ValidationResult(Boolean isValid) {
        this.isValid = isValid;
        this.validatableString = "";
    }

    public ValidationResult map(Function<ValidationResult, ValidationResult> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isValid) {
            return this;
        }
        return mapper.apply(this);
    }
}
