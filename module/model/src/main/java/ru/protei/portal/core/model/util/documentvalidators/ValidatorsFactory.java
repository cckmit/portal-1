package ru.protei.portal.core.model.util.documentvalidators;

import ru.protei.portal.core.model.helper.StringUtils;

import java.util.*;
import java.util.function.Function;

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

    static Map<String, Function<ValidationResult, ValidationResult>> mapOfValidators = new HashMap<>();

    static Function<ValidationResult, ValidationResult> organizationCodeValidator =
            new Validator(4, (String s) -> organizationCode.contains(s));
    static Function<ValidationResult, ValidationResult> TDtypeDocCodeValidator =
            new IntegerValidator(2, (Integer i) -> typeDocCode.contains(i));
    static Function<ValidationResult, ValidationResult> TDtypeProcessCodeValidator =
            new IntegerValidator(1, (Integer i) -> typeProcessCode.contains(i));
    static Function<ValidationResult, ValidationResult> TDtypeProcessWorkCodeValidator =
            new IntegerValidator(2, i -> (typeProcessWorkCode.contains(i)));
    static Function<ValidationResult, ValidationResult> TDfixCodeValidator =
            new Validator(true, 1, "Р"::equals);
    static Function<ValidationResult, ValidationResult> lengthTwoRussianLetterValidator =
            new Validator(2, s ->  s.matches("[А-Я][А-Я]"));
    static Function<ValidationResult, ValidationResult> endValidator =
            new Validator(0, StringUtils::isEmpty);

    static Function<ValidationResult, ValidationResult> PDdocNumberPartValidator = getPDdocNumberPartValidator();
    static Function<ValidationResult, ValidationResult> getPDdocNumberPartValidator() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#-", getOneSymbolValidator("-")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#1", getLengthMoreThanZeroIntegerValidator(1)));
        return getCompositeValidator(validateProcessList);
    };

    static Function<ValidationResult, ValidationResult> PDdocNumberValidator = getPDdocNumberValidator();
    static Function<ValidationResult, ValidationResult> getPDdocNumberValidator() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required# ", getOneSymbolValidator(" ")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#2", getLengthMoreThanZeroIntegerValidator(2)));
        validateProcessList.add(PDdocNumberPartValidator);
        return getCompositeValidator(validateProcessList);
    };

    static Function<ValidationResult, ValidationResult> getCompositeValidator(List<Function<ValidationResult, ValidationResult>> list) {
        return new Validator(true, value -> new ValidationResult(processValidation(value, list)));
    }

    static Function<ValidationResult, ValidationResult> getOneSymbolValidator(Boolean optional, String symbol) {
        return new Validator(optional, 1, symbol::equals);
    }

    static Function<ValidationResult, ValidationResult> getOneSymbolValidator(String symbol) {
        return getOneSymbolValidator(false, symbol);
    }

    static Function<ValidationResult, ValidationResult> getLengthMoreThanZeroIntegerValidator(Integer length) {
        return getLengthMoreThanZeroIntegerValidator(false, length);
    }

    static Function<ValidationResult, ValidationResult> getLengthMoreThanZeroIntegerValidator(Boolean optional, Integer length) {
        return new IntegerValidator(optional, length,  s -> 0 < s);
    }

    static ValidationResult processValidation(String value, List<Function<ValidationResult, ValidationResult>> validateProcessList) {
        ValidationResult validateString = new ValidationResult(value);
        for (Function<ValidationResult, ValidationResult> stringOptionalFunction : validateProcessList) {
            validateString = validateString.map(stringOptionalFunction);
        }
        return validateString;
    }
}
