package ru.protei.portal.ui.employee.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;

public interface AbstractEmployeeEditView extends IsWidget {

    void setActivity(AbstractEmployeeEditActivity activity);

    HasValue<String> firstName();
    HasValue<String> lastName();
    HasText secondName();

    HasText displayName();
    HasText shortName();

    HasValue<Date> birthDay ();

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

    HasValue<String> password();

    HasValue<String> confirmPassword();

    HasValue<EntityOption> company();

    HasValue<En_Gender> gender ();

    HasValue<String> locale();

    HasValue<Boolean> sendWelcomeEmail();

    HasValidable companyValidator();

    HasValidable firstNameValidator();

    HasValidable lastNameValidator();

    void setEmployeeLoginStatus(NameStatus status);

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

    HasText firstNameErrorLabel();

    HasText secondNameErrorLabel();

    HasText lastNameErrorLabel();

    HasText shortNameErrorLabel();

    String firstNameLabel();

    String secondNameLabel();

    String lastNameLabel();

    String shortNameLabel();

    String personalEmailLabel();

    String workEmailLabel();

    String loginLabel();

    HasText loginErrorLabel();

    HasEnabled saveEnabled();

    NameStatus getEmployeeLoginStatus();

    HasVisibility loginErrorLabelVisibility();

}
