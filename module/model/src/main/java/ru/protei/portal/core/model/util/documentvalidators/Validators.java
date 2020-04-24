package ru.protei.portal.core.model.util.documentvalidators;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class Validators {
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

    static Validator organizationCodeValidator = getSetContainsValidator(4, organizationCode);
    static Validator TDtypeDocCodeValidator = getSetContainsIntegerValidator(2, typeDocCode);
    static Validator TDtypeProcessCodeValidator = getSetContainsIntegerValidator(1, typeProcessCode);
    static Validator TDtypeProcessWorkCodeValidator = getSetContainsIntegerValidator(2, typeProcessWorkCode);
    static Validator TDfixCodeValidator = new Validator(true, getStringCheckWithLength(1, "Р"::equals));
    static Validator lengthTwoRussianLetterValidator = new Validator(getStringCheckWithLength(2, value ->  value.matches("[А-Я][А-Я]")));
    static Validator endValidator = new Validator(value -> new ValidationResult(value.length() == 0));

    static Validator PDdocNumberPartValidator = getPDdocNumberPartValidator();
    static Validator getPDdocNumberPartValidator() {
        List<Validator> validateProcessList = new ArrayList<>();
        validateProcessList.add(getOneSymbolValidator("-"));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(1));
        return new Validator(true, getCompositeCheck(validateProcessList));
    };

    static Validator PDdocNumberValidator = getPDdocNumberValidator();
    static Validator getPDdocNumberValidator() {
        List<Validator> validateProcessList = new ArrayList<>();
        validateProcessList.add(getOneSymbolValidator(" "));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(2));
        validateProcessList.add(PDdocNumberPartValidator);
        return new Validator(true, getCompositeCheck(validateProcessList));
    };

    static Validator getOneSymbolValidator(String symbol) {
        return new Validator(getStringCheckWithLength(1, symbol::equals));
    }

    static Validator getLengthMoreThanZeroIntegerValidator(Integer length) {
        return new Validator(getIntegerCheckWithLength(length, s -> 0 < s));
    }

    static Validator getSetContainsValidator(Integer valueLength, Set<String> set) {
        return new Validator(getStringCheckWithLength(valueLength, set::contains));
    }

    static Validator getSetContainsIntegerValidator(Integer valueLength, Set<Integer> set) {
        return new Validator(getIntegerCheckWithLength(valueLength, set::contains));
    }

    static Function<String, ValidationResult> getCompositeCheck(List<Validator> list) {
        return value -> processValidation(value, list);
    }

    static Function<String, ValidationResult> getStringCheckWithLength(Integer valueLength, Predicate<String> validationFunction) {
        return value -> {
            if (value.length() < valueLength) {
                return new ValidationResult(false);
            }
            if (validationFunction.test(value.substring(0, valueLength))) {
                return new ValidationResult(true, value.substring(valueLength));
            } else {
                return new ValidationResult(false);
            }
        };
    }

    static Function<String, ValidationResult> getIntegerCheckWithLength(Integer valueLength, Predicate<Integer> isValid) {
        return getStringCheckWithLength(valueLength, value -> {
            try {
                Integer number = Integer.parseInt(value);
                return isValid.test(number);
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }

    static ValidationResult processValidation(String value, List<Validator> validateProcessList) {
        ValidationResult validateString = new ValidationResult(value);
        for (Validator stringOptionalFunction : validateProcessList) {
            validateString = validateString.map(stringOptionalFunction);
        }
        return validateString;
    }
}
