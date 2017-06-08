package ru.protei.portal.ui.role.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;

/**
 * Представление создания и редактирования роли
 */
public interface AbstractRoleEditView extends IsWidget {
    void setActivity( AbstractRoleEditActivity activity );

    HasValue<String> firstName();
    HasValue<String> lastName();
    HasText secondName();

    HasText displayName();
    HasText shortName();

    HasValue<Date> birthDay ();

    HasText workPhone ();

    HasText homePhone ();

    HasText workEmail();

//    HasText personalEmail ();

    HasText workFax();

//    HasText homeFax();

    HasText workAddress ();

    HasText homeAddress ();

    HasText displayPosition ();

    HasText displayDepartment ();

    HasText personInfo ();

    HasText login();

    HasText password();

    HasText confirmPassword();

    HasValue<EntityOption> company();

    HasValue<En_Gender> gender ();

    HasValidable companyValidator();

    HasValidable firstNameValidator();

    HasValidable lastNameValidator();

    void setContactLoginStatus(NameStatus status);

    boolean isValidLogin();

    void showInfo( boolean isShow );
}
