package ru.protei.portal.core.model.util.documentvalidators;

import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DocumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.protei.portal.core.model.util.documentvalidators.ValidatorsFactory.*;

public class DocumentDecimalNumberValidator {

    static List<Function<ValidationResult, ValidationResult>> KDvalidateProcessList = createKDvalidateProcessList();
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

    static List<Function<ValidationResult, ValidationResult>> TDvalidateProcessList = createTDvalidateProcessList();
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

    static List<Function<ValidationResult, ValidationResult>> PDvalidateProcessList = createPDvalidateProcessList();
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

    static public boolean isValid(String value, En_DocumentCategory enDocumentCategory, DocumentType documentType) {
        switch (enDocumentCategory) {
            case KD:
                return processValidationIsValid(value, KDvalidateProcessList);

            case TD:
                return processValidationIsValid(value, TDvalidateProcessList);

            case PD:

                return processValidationIsValid(value, PDvalidateProcessList);

            default:
                return true;
        }
    }
}
