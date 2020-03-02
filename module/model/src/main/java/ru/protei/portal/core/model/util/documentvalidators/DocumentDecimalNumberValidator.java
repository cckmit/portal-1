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

    static Map<String, Function<String, Optional<String>>> mapOfValidators = new HashMap<>();

    static private Function<String, Optional<String>> organizationCodeValidator = new Validator(4, s -> organizationCode.contains(s));
    static private Function<String, Optional<String>> TDtypeDocCodeValidator = new IntegerValidator(2, i -> typeDocCode.contains(i));
    static private Function<String, Optional<String>> TDtypeProcessCodeValidator = new IntegerValidator(1, i -> typeProcessCode.contains(i));
    static private Function<String, Optional<String>> TDtypeProcessWorkCodeValidator = new IntegerValidator(2, i -> typeProcessWorkCode.contains(i));
    static private Function<String, Optional<String>> TDfixCodeValidator = new Validator(true, 1, "Р"::equals);
    static private Function<String, Optional<String>> lengthTwoRussianLetterValidator = new Validator(2, s -> s.matches("[А-Я][А-Я]")) ;
    static private Function<String, Optional<String>> endValidator = new Validator(0, StringUtils::isEmpty);

    static Function<String, Optional<String>> getPDdocNumberValidator(boolean length) {
        return new Validator(true, length ? 5 : 3,
                value -> {
                    List<Function<String, Optional<String>>> validateProcessList = new ArrayList<>();
                    validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required# ", getOneSymbolValidator(" ")));
                    validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#2", getLengthMoreThanZeroIntegerValidator(2)));
                    validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#optional#-", getOneSymbolValidator(true, "-")));
                    validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#optional#1", getLengthMoreThanZeroIntegerValidator(true, 1)));
                    return processValidation(value, validateProcessList);
                });
    }

    static Function<String, Optional<String>> getFlatMapEndValidator(Function<String, Function<String, Optional<String>>> func) {
        return new Validator(0,
                value -> Optional.of(value)
                        .flatMap(func.apply(value))
                        .flatMap(endValidator)
                        .isPresent());
    }

    static Function<String, Optional<String>> getOneSymbolValidator(Boolean optional, String symbol) {
        return new Validator(optional, 1, symbol::equals);
    }

    static Function<String, Optional<String>> getOneSymbolValidator(String symbol) {
        return getOneSymbolValidator(false, symbol);
    }

    static Function<String, Optional<String>> getLengthMoreThanZeroIntegerValidator(Integer length) {
        return getLengthMoreThanZeroIntegerValidator(false, length);
    }

    static Function<String, Optional<String>> getLengthMoreThanZeroIntegerValidator(Boolean optional, Integer length) {
        return new IntegerValidator(optional, length, s -> 0 < s);
    }

    static List<Function<String, Optional<String>>> createKDvalidateProcessList() {
        List<Function<String, Optional<String>>> validateProcessList = new ArrayList<>();
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

    static List<Function<String, Optional<String>>> createTDvalidateProcessList() {
        List<Function<String, Optional<String>>> validateProcessList = new ArrayList<>();
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

    static List<Function<String, Optional<String>>> createPDvalidateProcessList() {
        List<Function<String, Optional<String>>> validateProcessList = new ArrayList<>();
        validateProcessList.add(organizationCodeValidator);
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#.", getOneSymbolValidator(".")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#5", getLengthMoreThanZeroIntegerValidator(5)));
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required#-", getOneSymbolValidator("-")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#2", getLengthMoreThanZeroIntegerValidator(2)));
        validateProcessList.add(mapOfValidators.getOrDefault("oneSymbolValidator#required# ", getOneSymbolValidator(" ")));
        validateProcessList.add(mapOfValidators.getOrDefault("getLengthMoreThanZeroIntegerValidator#required#2", getLengthMoreThanZeroIntegerValidator(2)));
        validateProcessList.add(getFlatMapEndValidator(value -> getPDdocNumberValidator(value.length() > 3)));
        return validateProcessList;
    }

    static boolean processValidation(String value, List<Function<String, Optional<String>>> validateProcessList) {
        Optional<String> validateString = Optional.of(value);
        for (Function<String, Optional<String>> stringOptionalFunction : validateProcessList) {
            validateString = validateString.flatMap(stringOptionalFunction);
        }
        return validateString.isPresent();
    }

    static public boolean isValid(String value, En_DocumentCategory enDocumentCategory) {
        switch (enDocumentCategory) {
            case KD:
                return processValidation(value, createKDvalidateProcessList());

            case TD:
                return processValidation(value, createTDvalidateProcessList());

            case PD:
                return processValidation(value, createPDvalidateProcessList());

            default:
                return true;
        }
    }
}
