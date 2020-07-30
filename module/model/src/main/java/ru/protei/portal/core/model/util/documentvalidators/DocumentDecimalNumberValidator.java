package ru.protei.portal.core.model.util.documentvalidators;

import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.util.Validator;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.util.documentvalidators.Validators.*;

public class DocumentDecimalNumberValidator {

    static List<Validator> KDvalidateProcessList = createKDvalidateProcessList();
    static List<Validator> createKDvalidateProcessList() {
        List<Validator> validateProcessList = new ArrayList<>();
        validateProcessList.add(organizationCodeValidator);
        validateProcessList.add(getOneSymbolValidator("."));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(6));
        validateProcessList.add(getOneSymbolValidator("."));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(3));
        validateProcessList.add(getOneSymbolValidator("-"));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(2));
        validateProcessList.add(lengthTwoRussianLetterValidator);
        validateProcessList.add(endValidator);
        return validateProcessList;
    }

    static List<Validator> TDvalidateProcessList = createTDvalidateProcessList();
    static List<Validator> createTDvalidateProcessList() {
        List<Validator> validateProcessList = new ArrayList<>();
        validateProcessList.add(organizationCodeValidator);
        validateProcessList.add(getOneSymbolValidator("."));
        validateProcessList.add(TDtypeDocCodeValidator);
        validateProcessList.add(TDtypeProcessCodeValidator);
        validateProcessList.add(TDtypeProcessWorkCodeValidator);
        validateProcessList.add(getOneSymbolValidator("."));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(5));
        validateProcessList.add(TDfixCodeValidator);
        validateProcessList.add(endValidator);
        return validateProcessList;
    }

    static List<Validator> PDvalidateProcessList = createPDvalidateProcessList();
    static List<Validator> createPDvalidateProcessList() {
        List<Validator> validateProcessList = new ArrayList<>();
        validateProcessList.add(organizationCodeValidator);
        validateProcessList.add(getOneSymbolValidator("."));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(5));
        validateProcessList.add(getOneSymbolValidator("-"));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(2));
        validateProcessList.add(getOneSymbolValidator(" "));
        validateProcessList.add(getLengthMoreThanZeroIntegerValidator(2));
        validateProcessList.add(PDdocNumberValidator);
        validateProcessList.add(endValidator);
        return validateProcessList;
    }

    static boolean processValidationIsValid(String value, List<Validator> validateProcessList) {
        return processValidation(value, validateProcessList).isValid();
    }

    static public boolean isValid(String value, En_DocumentCategory enDocumentCategory) {
        if (enDocumentCategory == null) {
            return false;
        }
        switch (enDocumentCategory) {
            case KD:
                if (value == null) {
                    return false;
                }
                return processValidationIsValid(value, KDvalidateProcessList);

            case TD:
                if (value == null) {
                    return false;
                }
                return processValidationIsValid(value, TDvalidateProcessList);

            case PD:
                if (value == null) {
                    return false;
                }
                return processValidationIsValid(value, PDvalidateProcessList);

            default:
                return true;
        }
    }
}
