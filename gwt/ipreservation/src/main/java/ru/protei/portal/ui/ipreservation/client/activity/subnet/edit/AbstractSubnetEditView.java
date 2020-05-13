package ru.protei.portal.ui.ipreservation.client.activity.subnet.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Абстракция вида карточки создания/редактирования подсети
 */
public interface AbstractSubnetEditView extends IsWidget {

    void setActivity(AbstractSubnetEditActivity activity);

    void setAddressStatus (NameStatus status);
    HasValue<String> address();
    HasValue<String> mask();
    HasText comment();

    HasValidable addressValidator();

    HasEnabled maskEnabled ();
    HasEnabled addressEnabled ();
    HasEnabled saveEnabled();

    HasVisibility saveVisibility();
}
