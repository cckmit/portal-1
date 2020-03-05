package ru.protei.portal.core.model.util.documentvalidators;

import java.util.Objects;
import java.util.function.Function;

class ValidationResult {
    Boolean isValid;
    String validatableString;
    Integer countOfProcessed;

    public ValidationResult(Boolean isValid, String validatableString, Integer countOfProcessed) {
        this.isValid = isValid;
        this.validatableString = validatableString;
        this.countOfProcessed = countOfProcessed;
    }

    public ValidationResult(String validatableString) {
        this.isValid = true;
        this.validatableString = validatableString;
        this.countOfProcessed = 0;
    }

    public ValidationResult(Boolean isValid) {
        this.isValid = isValid;
        this.validatableString = "";
        this.countOfProcessed = 0;
    }

    public ValidationResult(ValidationResult other) {
        isValid = other.isValid;
        validatableString = other.validatableString;
        countOfProcessed = (other.isValid) ? other.countOfProcessed : 0;
    }

    public ValidationResult map(Function<ValidationResult, ValidationResult> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isValid) {
            return this;
        }
        ValidationResult apply = mapper.apply(this);
        if (apply.isValid) {
            apply.countOfProcessed += countOfProcessed;
        }
        return apply;
    }
}
