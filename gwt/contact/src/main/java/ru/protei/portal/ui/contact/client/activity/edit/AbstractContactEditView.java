package ru.protei.portal.ui.contact.client.activity.edit;

import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;

/**
 * Представление создания и редактирования контактного лица
 */
public interface AbstractContactEditView extends IsWidget {
    void setActivity( AbstractContactEditActivity activity );

    HasValue<String> firstName();
    HasValue<String> lastName();
    HasText secondName();

    HasText displayName();
    HasText shortName();

    HasValue<Date> birthDay ();

    void setBirthDayTimeZone(TimeZone timeZone);

    HasText workPhone ();

    HasText homePhone ();

    HasText mobilePhone();

    HasText workEmail();

    HasText personalEmail();

    HasText workFax();

    HasText homeFax();

    HasText workAddress ();

    HasText homeAddress ();

    HasText displayPosition ();

    HasText displayDepartment ();

    HasText personInfo ();

    HasText login();

    HasText loginErrorLabel();

    HasValue<String> password();

    HasValue<String> confirmPassword();

    HasValue<EntityOption> company();

    HasValue<En_Gender> gender ();

    HasValue<String> locale();

    HasValue<Boolean> sendWelcomeEmail();

    HasValidable companyValidator();

    HasValidable firstNameValidator();

    HasValidable lastNameValidator();

    void setContactLoginStatus(NameStatus status);

    void showInfo( boolean isShow );

    HasVisibility saveVisibility();

    HasEnabled companyEnabled ();

    HasVisibility fireBtnVisibility();

    HasVisibility firedMsgVisibility();

    HasVisibility deletedMsgVisibility();

    HasVisibility sendWelcomeEmailVisibility();

    HasVisibility sendEmailWarningVisibility();

    HasValidable workEmailValidator();

    HasValidable personalEmailValidator();

    HasVisibility firstNameErrorLabelVisibility();

    HasVisibility secondNameErrorLabelVisibility();

    HasVisibility lastNameErrorLabelVisibility();

    HasVisibility shortNameErrorLabelVisibility();

    String firstNameLabel();

    String secondNameLabel();

    String lastNameLabel();

    String shortNameLabel();

    String personalEmailLabel();

    String workEmailLabel();

    String loginLabel();

    HasEnabled saveEnabled();

    NameStatus getContactLoginStatus();

    HasVisibility loginErrorLabelVisibility();

}
