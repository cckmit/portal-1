package ru.protei.portal.ui.common.client.lang;

import com.google.gwt.i18n.client.Messages;

/**
 * Интерфейс со строковыми константами
 */
public interface Lang extends Messages {
    String buttonLogout();

    String buttonLogin();

    String companies();

    String name();

    String created();

    String updated();

    String sortBy();

    String products();

    String productNew();

    String productShowDeprecated();

    String buttonSave();

    String buttonCancel();

    String buttonCreate();

    String errNoMatchesFound();

    String errGetList();

    String msgHello();

    String unknownField();

    String companyGroup();

    String search();

    String description();

    String productToArchive();

    String productName();

    String msgObjectSaved();

    String productFromArchive();

    String errEmptyName();

    String error();

    String companyName();

    String companyActualAddress();

    String companyLegalAddress();

    String companyWebSite();

    String phone();

    String comment();

    String contacts();

    String companyNew();

    String errAsteriskRequired();

    String email();

    String errLoginOrPwd();

    String msgOK();

    String errConnectionError();

    String errInvalidSessionID();

    String errGetDataError();

    String errNotFound();

    String errNotCreated();

    String errNotUpdated();

    String errNotSaved();

    String errUndefinedObject();

    String errAlreadyExist();

    String errValidationError();

    String errIncorrectParams();

    String errInternalError();

    String errUnknownResult();

    String companyInfoHeader();

    String companyCommonHeader();

    String productDescription();

    String company();

    String showFired();

    String contactFullName();

    String contactPosition();

    String edit();

    String companyAdditionalInfoHeader();

    String companyEdit();

    String noCompanyGroup();
}
