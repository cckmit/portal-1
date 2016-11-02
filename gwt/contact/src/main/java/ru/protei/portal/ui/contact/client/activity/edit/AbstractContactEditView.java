package ru.protei.portal.ui.contact.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;

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

}
