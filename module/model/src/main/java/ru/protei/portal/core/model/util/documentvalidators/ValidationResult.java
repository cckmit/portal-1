package ru.protei.portal.core.model.util.documentvalidators;

import java.util.Objects;
import java.util.function.Function;

class ValidationResult {
    Boolean isValid;
    String validatableString;
    Integer processedLength;
    Integer countOfProcessed = 0;

    public ValidationResult(String validatableString, Integer processedLength) {
        this.isValid = true;
        this.validatableString = validatableString;
        this.processedLength = processedLength;
    }

    public ValidationResult(Boolean isValid, Integer processedLength, int countOfProcessed) {
        this.isValid = isValid;
        this.processedLength = processedLength;
        this.countOfProcessed = countOfProcessed;
    }

    public ValidationResult(String validatableString) {
        this.isValid = true;
        this.validatableString = validatableString;
    }

    public ValidationResult(Boolean isValid) {
        this.isValid = isValid;
    }

    public ValidationResult(ValidationResult other) {
        isValid = other.isValid;
        processedLength = (other.isValid) ?  other.countOfProcessed : 0;
        countOfProcessed = 0;
    }

    public ValidationResult map(Function<ValidationResult, ValidationResult> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isValid) {
            return this;
        }
        ValidationResult apply = mapper.apply(this);
        if (apply.isValid) {
            apply.countOfProcessed = countOfProcessed + apply.processedLength;
        }
        return apply;
    }
}
