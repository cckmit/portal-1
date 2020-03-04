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

    static Function<ValidationResult, ValidationResult> organizationCodeValidator = getSetContainsValidator(4, organizationCode);
    static Function<ValidationResult, ValidationResult> TDtypeDocCodeValidator = getSetContainsIntegerValidator(2, typeDocCode);
    static Function<ValidationResult, ValidationResult> TDtypeProcessCodeValidator = getSetContainsIntegerValidator(1, typeProcessCode);
    static Function<ValidationResult, ValidationResult> TDtypeProcessWorkCodeValidator = getSetContainsIntegerValidator(2, typeProcessWorkCode);
    static Function<ValidationResult, ValidationResult> TDfixCodeValidator = new Validator(true, 1, "Р"::equals);
    static Function<ValidationResult, ValidationResult> lengthTwoRussianLetterValidator = new Validator(2, s ->  s.matches("[А-Я][А-Я]"));
    static Function<ValidationResult, ValidationResult> endValidator = new Validator(0, StringUtils::isEmpty);

    static Function<ValidationResult, ValidationResult> PDdocNumberPartValidator = getPDdocNumberPartValidator();
    static Function<ValidationResult, ValidationResult> getPDdocNumberPartValidator() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(getOneSymbolValidator("-"));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(1));
        return getCompositeValidator(validateProcessList);
    };

    static Function<ValidationResult, ValidationResult> PDdocNumberValidator = getPDdocNumberValidator();
    static Function<ValidationResult, ValidationResult> getPDdocNumberValidator() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(getOneSymbolValidator(" "));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(2));
        validateProcessList.add(PDdocNumberPartValidator);
        return getCompositeValidator(validateProcessList);
    };

    static Function<ValidationResult, ValidationResult> getOneSymbolValidator(String symbol) {
        return new Validator(1, symbol::equals);
    }

    static Function<ValidationResult, ValidationResult> getLengthMoreThanZeroIntegerValidator(Integer length) {
        return new IntegerValidator(false, length,  s -> 0 < s);
    }

    static Function<ValidationResult, ValidationResult> getSetContainsValidator(Integer valueLength, Set<String> set) {
        return new Validator(valueLength, set::contains);
    }

    static Function<ValidationResult, ValidationResult> getSetContainsIntegerValidator(Integer valueLength, Set<Integer> set) {
        return new IntegerValidator(valueLength, set::contains);
    }

    static Function<ValidationResult, ValidationResult> getCompositeValidator(List<Function<ValidationResult, ValidationResult>> list) {
        return new Validator(true, value -> new ValidationResult(processValidation(value, list)));
    }

    static ValidationResult processValidation(String value, List<Function<ValidationResult, ValidationResult>> validateProcessList) {
        ValidationResult validateString = new ValidationResult(value);
        for (Function<ValidationResult, ValidationResult> stringOptionalFunction : validateProcessList) {
            validateString = validateString.map(stringOptionalFunction);
        }
        return validateString;
    }
}
