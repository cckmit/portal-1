package ru.protei.portal.ui.ipreservation.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Абстракция вида карточки редактирования зарезервированного IP
 */
public interface AbstractReservedIpEditView extends IsWidget {

    void setActivity(AbstractReservedIpEditActivity activity);

    void setAddress(String value);
    //HasValue<String> ipAddress();
    HasValue<String> macAddress();
    HasText comment();
    HasValue<Subnet> subnet();
    HasValue<PersonShortView> owner();

    HasValidable macAddressValidator();

    HasVisibility saveVisibility();

    HasEnabled saveEnabled();
}
