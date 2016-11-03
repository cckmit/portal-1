package ru.protei.portal.ui.contact.client.activity.edit;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.ent.Company;

/**
 * Created by michael on 02.11.16.
 */
public interface AbstractContactEditView extends IsWidget {
    void setActivity( AbstractContactEditActivity activity );

    HasText firstName();
    HasText lastName();
    HasText secondName();

    HasText displayName();
    HasText shortName();

    SinglePicker birthDay ();

    HasText workPhone ();

    HasText homePhone ();

    HasText workEmail();

    HasText personalEmail ();

    HasText workFax();

    HasText homeFax();

    HasText workAddress ();

    HasText homeAddress ();

    HasText displayPosition ();

    HasText displayDepartment ();

    HasText personInfo ();

    HasValue<Company> company();
}
