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

    String errDatabaseError ();

    String errDatabaseTempError ();

    String errUnknownResult();

    String companyInfoHeader();

    String companyCommonHeader();

    String company();

    String contactShowFired();

    String contactFullName();

    String contactPosition();

    String fullScreen();

    String contactLastName();

    String contactFirstName();

    String contactSecondName();

    String contactDepartment();

    String fax();

    String address();
    
    String productDescription();

    String edit();

    String companyAdditionalInfoHeader();

    String newContact ();

    String editContactHeader (String contactName);

    String firstName ();

    String lastName ();

    String secondName ();

    String displayName ();

    String displayShortName ();

    String birthday ();

    String gender ();

    String genderMale ();
    String genderFemale ();
    String genderUndefined ();

    String personalData ();
    String contactInfo ();
    String advPersonalData ();
    String workPhone ();
    String personalPhone ();
    String workEmail ();
    String personalEmail ();
    String primaryFax ();
    String secondaryFax ();

    String workAddress ();
    String homeAddress ();
    String department ();

    String errorCompanyRequired ();
    String errorFirstNameRequired();
    String errorLastNameRequired ();

    String companyCategory();

    String companyGroupLabel ();
}
