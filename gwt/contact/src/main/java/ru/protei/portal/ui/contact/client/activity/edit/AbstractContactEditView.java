package ru.protei.portal.ui.contact.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

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

    Selector<Company> company();

    Selector<En_Gender> gender ();
}
