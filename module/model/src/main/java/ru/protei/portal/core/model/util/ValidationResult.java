package ru.protei.portal.core.model.util;

import java.util.Objects;
import java.util.function.Function;

public class ValidationResult {
    private String message;
    private final boolean isValid;

    public Boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult error() {
        return new ValidationResult(false, null);
    }

    public static ValidationResult error(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

    public ValidationResult withMessage(String message) {
        this.message = message;
        return this;
    }

    public ValidationResult map(Function<ValidationResult, ValidationResult> mapper) {
        Objects.requireNonNull(mapper);
        if (!isValid()) {
            return this;
        }
        return mapper.apply(this);
    }

    ValidationResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

}
