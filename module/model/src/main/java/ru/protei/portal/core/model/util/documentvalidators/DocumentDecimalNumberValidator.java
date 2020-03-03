package ru.protei.portal.core.model.util.documentvalidators;

import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.*;
import java.util.function.Function;

public class DocumentDecimalNumberValidator {
    static private Set<String> organizationCode = new HashSet<>(Arrays.asList(
            "ПАМР",
            "ПДРА"
    ));
    static private Set<Integer> typeDocCode = new HashSet<>(Arrays.asList(
            1,
            2,
            4,
            5,
            6,
            7,
            9,
            10,
            20,
            25,
            30,
            40,
            41,
            42,
            43,
            44,
            45,
            46,
            47,
            48,
            50,
            55,
            57,
            59,
            60,
            62,
            66,
            67,
            70,
            71,
            72,
            75,
            77,
            78,
            79,
            80
    ));
    static private Set<Integer> typeProcessCode = new HashSet<>(Arrays.asList(
            0,
            1,
            2,
            3
    ));
    static private Set<Integer> typeProcessWorkCode = new HashSet<>(Arrays.asList(
            0,
            1,
            2, 3,
            4,
            6, 7,
            8,
            10,
            21,
            41, 42,
            50, 51,
            55,
            60,
            65,
            71,
            73, 74,
            75,
            80, 81,
            85,
            88,
            90, 91
    ));

    static Map<String, Function<ValidationResult, ValidationResult>> mapOfValidators = new HashMap<>();

    static private Function<ValidationResult, ValidationResult> organizationCodeValidator
            = new Validator(4, (String s) -> organizationCode.contains(s));
    static private Function<ValidationResult, ValidationResult> TDtypeDocCodeValidator =
            new IntegerValidator(2, (Integer i) -> typeDocCode.contains(i));
    static private Function<ValidationResult, ValidationResult> TDtypeProcessCodeValidator =
            new IntegerValidator(1, (Integer i) -> typeProcessCode.contains(i));
    static private Function<ValidationResult, ValidationResult> TDtypeProcessWorkCodeValidator =
            new IntegerValidator(2, i -> (typeProcessWorkCode.contains(i)));
    static private Function<ValidationResult, ValidationResult> TDfixCodeValidator =
            new Validator(true, 1, "Р"::equals);
    static private Function<ValidationResult, ValidationResult> lengthTwoRussianLetterValidator =
            new Validator(2, s ->  s.matches("[А-Я][А-Я]"));
    static private Function<ValidationResult, ValidationResult> endValidator =
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

    static List<Function<ValidationResult, ValidationResult>> createKDvalidateProcessList() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(organizationCodeValidator);
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#.", getOneSymbolValidator(".")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#6", getLengthMoreThanZeroIntegerValidator(6)));
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#.", getOneSymbolValidator(".")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#3", getLengthMoreThanZeroIntegerValidator(3)));
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#-", getOneSymbolValidator("-")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#2", getLengthMoreThanZeroIntegerValidator(2)));
        validateProcessList.add(lengthTwoRussianLetterValidator);
        validateProcessList.add(endValidator);
        return validateProcessList;
    }

    static List<Function<ValidationResult, ValidationResult>> createTDvalidateProcessList() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(organizationCodeValidator);
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#.", getOneSymbolValidator(".")));
        validateProcessList.add(TDtypeDocCodeValidator);
        validateProcessList.add(TDtypeProcessCodeValidator);
        validateProcessList.add(TDtypeProcessWorkCodeValidator);
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#.", getOneSymbolValidator(".")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#5", getLengthMoreThanZeroIntegerValidator(5)));
        validateProcessList.add(TDfixCodeValidator);
        validateProcessList.add(endValidator);
        return validateProcessList;
    }

    static List<Function<ValidationResult, ValidationResult>> createPDvalidateProcessList() {
        List<Function<ValidationResult, ValidationResult>> validateProcessList = new ArrayList<>();
        validateProcessList.add(organizationCodeValidator);
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#.", getOneSymbolValidator(".")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#5", getLengthMoreThanZeroIntegerValidator(5)));
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#-", getOneSymbolValidator("-")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#2", getLengthMoreThanZeroIntegerValidator(2)));
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required# ", getOneSymbolValidator(" ")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#2", getLengthMoreThanZeroIntegerValidator(2)));
        validateProcessList.add(PDdocNumberValidator);
        validateProcessList.add(endValidator);
        return validateProcessList;
    }

    static boolean processValidationIsValid(String value, List<Function<ValidationResult, ValidationResult>> validateProcessList) {
        return processValidation(value, validateProcessList).isValid;
    }

    static ValidationResult processValidation(String value, List<Function<ValidationResult, ValidationResult>> validateProcessList) {
        ValidationResult validateString = new ValidationResult(value);
        for (Function<ValidationResult, ValidationResult> stringOptionalFunction : validateProcessList) {
            validateString = validateString.map(stringOptionalFunction);
        }
        return validateString;
    }

    static public boolean isValid(String value, En_DocumentCategory enDocumentCategory) {
        switch (enDocumentCategory) {
            case KD:
                return processValidationIsValid(value, createKDvalidateProcessList());

            case TD:
                return processValidationIsValid(value, createTDvalidateProcessList());

            case PD:
                return processValidationIsValid(value, createPDvalidateProcessList());

            default:
                return true;
        }
    }

    static class ValidationResult {
        Boolean isValid;
        String value;
        Integer processedLength;
        Integer count = 0;

        public ValidationResult(String value, Integer processedLength) {
            this.isValid = true;
            this.value = value;
            this.processedLength = processedLength;
        }

        public ValidationResult(Boolean isValid, Integer processedLength, int count) {
            this.isValid = isValid;
            this.processedLength = processedLength;
            this.count = count;
        }

        public ValidationResult(String value) {
            this.isValid = true;
            this.value = value;
        }

        public ValidationResult() {
            this.isValid = false;
        }

        public ValidationResult(ValidationResult other) {
            isValid = other.isValid;
            processedLength = (other.isValid) ?  other.count : 0;
            count = 0;
        }

        public ValidationResult map(Function<ValidationResult, ValidationResult> mapper) {
            Objects.requireNonNull(mapper);
            if (!this.isValid) {
                return this;
            }
            ValidationResult apply = mapper.apply(this);
            if (apply.isValid) {
                apply.count = count + apply.processedLength;
            }
            return apply;
        }
    }
}
