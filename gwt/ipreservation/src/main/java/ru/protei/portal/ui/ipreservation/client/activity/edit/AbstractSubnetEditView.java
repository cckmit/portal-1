package ru.protei.portal.ui.ipreservation.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Абстракция вида карточки создания/редактирования подсети
 */
public interface AbstractSubnetEditView extends IsWidget {

    void setActivity(AbstractSubnetEditActivity activity);

    HasValue<String> address();
    HasValue<String> mask();
    HasValue<Boolean> local();
    HasText comment();

    HasValidable addressValidator();
    HasValidable maskValidator();

    HasEnabled saveEnabled();

    HasVisibility saveVisibility();
}
