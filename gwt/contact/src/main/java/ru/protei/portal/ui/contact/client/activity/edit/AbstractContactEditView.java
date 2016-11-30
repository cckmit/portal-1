package ru.protei.portal.ui.contact.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;

/**
 * Created by michael on 02.11.16.
 */
public interface AbstractContactEditView extends IsWidget {
    void setActivity( AbstractContactEditActivity activity );

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

    HasValue<EntityOption> company();

    HasValue<En_Gender> gender ();

    HasValidable companyValidator();

    HasValidable firstNameValidator();

    HasValidable lastNameValidator();
}