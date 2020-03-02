package ru.protei.portal.core.model.util.documentvalidators;

import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

    static private Function<String, Optional<String>> dotValidator = new Validator(1, "."::equals);
    static private Function<String, Optional<String>> dashValidator = new Validator(1, "-"::equals);
    static private Function<String, Optional<String>> spaceValidator = new Validator(1, " "::equals);
    static private Function<String, Optional<String>> endValidator = new Validator(0, StringUtils::isEmpty);
    static private Function<String, Optional<String>> organizationCodeValidator = new Validator(4, s -> organizationCode.contains(s));
    static private Function<String, Optional<String>> TDtypeDocCodeValidator = new IntegerValidator(2, i -> typeDocCode.contains(i));
    static private Function<String, Optional<String>> TDtypeProcessCodeValidator = new IntegerValidator(1, i -> typeProcessCode.contains(i));
    static private Function<String, Optional<String>> TDtypeProcessWorkCodeValidator = new IntegerValidator(2, i -> typeProcessWorkCode.contains(i));
    static private Function<String, Optional<String>> TDfixCodeValidator = new Validator(true, 1, "Р"::equals);
    static private Function<String, Optional<String>> lengthSixMoreThanZeroIntegerValidator = new IntegerValidator(6, s -> 0 < s) ;
    static private Function<String, Optional<String>> lengthFiveMoreThanZeroIntegerValidator = new IntegerValidator(5, s -> 0 < s) ;
    static private Function<String, Optional<String>> lengthThreeMoreThanZeroIntegerValidator = new IntegerValidator(3, s -> 0 < s) ;
    static private Function<String, Optional<String>> lengthTwoMoreThanZeroIntegerValidator = new IntegerValidator(2, s -> 0 < s) ;
    static private Function<String, Optional<String>> lengthOneMoreThanZeroIntegerValidator = new IntegerValidator(1, s -> 0 < s) ;
    static private Function<String, Optional<String>> lengthTwoRussianLetterValidator = new Validator(2, s -> s.matches("[А-Я][А-Я]")) ;

    static public boolean isValid(String value, En_DocumentCategory enDocumentCategory) {
        switch (enDocumentCategory) {
            case KD:
                return Optional.of(value)
                        .flatMap(organizationCodeValidator)
                        .flatMap(dotValidator)
                        .flatMap(lengthSixMoreThanZeroIntegerValidator)
                        .flatMap(dotValidator)
                        .flatMap(lengthThreeMoreThanZeroIntegerValidator)
                        .flatMap(dashValidator)
                        .flatMap(lengthTwoMoreThanZeroIntegerValidator)
                        .flatMap(lengthTwoRussianLetterValidator)
                        .flatMap(endValidator)
                        .isPresent();

            case PD:
                return Optional.of(value)
                        .flatMap(organizationCodeValidator)
                        .flatMap(dotValidator)
                        .flatMap(lengthFiveMoreThanZeroIntegerValidator)
                        .flatMap(dashValidator)
                        .flatMap(lengthTwoMoreThanZeroIntegerValidator)
                        .flatMap(spaceValidator)
                        .flatMap(lengthTwoMoreThanZeroIntegerValidator)
                        .flatMap(spaceValidator)
                        .flatMap(lengthOneMoreThanZeroIntegerValidator)
                        .flatMap(endValidator)
                        .isPresent();

            case TD:
                return Optional.of(value)
                        .flatMap(organizationCodeValidator)
                        .flatMap(dotValidator)
                        .flatMap(TDtypeDocCodeValidator)
                        .flatMap(TDtypeProcessCodeValidator)
                        .flatMap(TDtypeProcessWorkCodeValidator)
                        .flatMap(dotValidator)
                        .flatMap(lengthFiveMoreThanZeroIntegerValidator)
                        .flatMap(TDfixCodeValidator)
                        .flatMap(endValidator)
                        .isPresent();
            default:
                return true;
        }
    }
}
