package ru.protei.portal.core.model.util.documentvalidators;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValidatorsFactory {
    static private Set<String> organizationCode = new HashSet<>(Arrays.asList(
            "ПАМР", "ПДРА"
    ));
    static private Set<Integer> typeDocCode = new HashSet<>(Arrays.asList(
            1, 2, 4, 5, 6, 7, 9, 10, 20, 25, 30, 40, 41, 42, 43, 44, 45, 46, 47, 48, 50, 55,
            57, 59, 60, 62, 66, 67, 70, 71, 72, 75, 77, 78, 79, 80
    ));
    static private Set<Integer> typeProcessCode = new HashSet<>(Arrays.asList(
            0, 1, 2, 3
    ));
    static private Set<Integer> typeProcessWorkCode = new HashSet<>(Arrays.asList(
            0, 1, 2, 3, 4, 6, 7, 8, 10, 21, 41, 42, 50, 51, 55, 60, 65, 71, 73, 74, 75, 80, 81, 85, 88, 90, 91
    ));

    static Function<ValidationResult, ValidationResult> organizationCodeValidator = getSetContainsValidator(4, organizationCode);
    static Function<ValidationResult, ValidationResult> TDtypeDocCodeValidator = getSetContainsIntegerValidator(2, typeDocCode);
    static Function<ValidationResult, ValidationResult> TDtypeProcessCodeValidator = getSetContainsIntegerValidator(1, typeProcessCode);
    static Function<ValidationResult, ValidationResult> TDtypeProcessWorkCodeValidator = getSetContainsIntegerValidator(2, typeProcessWorkCode);
    static Function<ValidationResult, ValidationResult> TDfixCodeValidator = getSimpleValidator(true, 1, "Р"::equals);
    static Function<ValidationResult, ValidationResult> lengthTwoRussianLetterValidator = getSimpleValidator(2, s ->  s.matches("[А-Я][А-Я]"));
    static Function<ValidationResult, ValidationResult> endValidator = new Validator((String s) -> new ValidationResult(s.length() == 0, 0));

    static Function<ValidationResult, ValidationResult> PDdocNumberPartValidator = getPDdocNumberPartValidator();
    static Function<ValidationResult, ValidationResult> getPDdocNumberPartValidator() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(getOneSymbolValidator("-"));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(1));
        return getCompositeValidator(true, validateProcessList);
    };

    static Function<ValidationResult, ValidationResult> PDdocNumberValidator = getPDdocNumberValidator();
    static Function<ValidationResult, ValidationResult> getPDdocNumberValidator() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(getOneSymbolValidator(" "));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(2));
        validateProcessList.add(PDdocNumberPartValidator);
        return getCompositeValidator(true, validateProcessList);
    };

    static Function<ValidationResult, ValidationResult> getOneSymbolValidator(String symbol) {
        return getSimpleValidator(1, symbol::equals);
    }

    static Function<ValidationResult, ValidationResult> getLengthMoreThanZeroIntegerValidator(Integer length) {
        return getSimpleIntegerValidator(length, s -> 0 < s);
    }

    static Function<ValidationResult, ValidationResult> getSetContainsValidator(Integer valueLength, Set<String> set) {
        return getSimpleValidator(valueLength, set::contains);
    }

    static Function<ValidationResult, ValidationResult> getSetContainsIntegerValidator(Integer valueLength, Set<Integer> set) {
        return getSimpleIntegerValidator(valueLength, set::contains);
    }

    static Function<ValidationResult, ValidationResult> getCompositeValidator(Boolean optional, List<Function<ValidationResult, ValidationResult>> list) {
        return new Validator(optional, value -> new ValidationResult(processValidation(value, list)));
    }

    static ValidationResult processValidation(String value, List<Function<ValidationResult, ValidationResult>> validateProcessList) {
        ValidationResult validateString = new ValidationResult(value);
        for (Function<ValidationResult, ValidationResult> stringOptionalFunction : validateProcessList) {
            validateString = validateString.map(stringOptionalFunction);
        }
        return validateString;
    }

    static Validator getSimpleValidator(Boolean optional, Integer valueLength, Predicate<String> validationFunction) {
        return new Validator(optional, (String value) -> {
            if (value.length() < valueLength) {
                return new ValidationResult(false);
            }

            if (validationFunction.test(value.substring(0, valueLength))) {
                return new ValidationResult(value.substring(valueLength), valueLength);
            } else {
                return new ValidationResult(false);
            }
        });
    }

    static Validator getSimpleValidator(Integer valueLength, Predicate<String> validationFunction) {
        return new Validator(false, (String value) -> {
            if (value.length() < valueLength) {
                return new ValidationResult(false);
            }

            if (validationFunction.test(value.substring(0, valueLength))) {
                return new ValidationResult(value.substring(valueLength), valueLength);
            } else {
                return new ValidationResult(false);
            }
        });
    }

    static Validator getSimpleIntegerValidator(Boolean optional, Integer valueLength, Predicate<Integer> isValid) {
        return getSimpleValidator(optional, valueLength, (String s) -> {
            try {
                Integer number = Integer.parseInt(s);
                return isValid.test(number);
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }

    static Validator getSimpleIntegerValidator(Integer valueLength, Predicate<Integer> isValid) {
        return getSimpleValidator(valueLength, (String s) -> {
            try {
                Integer number = Integer.parseInt(s);
                return isValid.test(number);
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }
}
