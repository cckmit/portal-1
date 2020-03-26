package ru.protei.portal.ui.ipreservation.client.activity.reservedip.create;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

/**
 * Абстракция вида карточки резервирования IP адресов
 */
public interface AbstractReservedIpCreateView extends IsWidget {

    void setActivity(AbstractReservedIpCreateActivity activity);

    HasValue<String> ipAddress();
    HasValue<String> macAddress();
    HasText comment();
    HasValue<Set<SubnetOption>> subnets();
    HasValue<PersonShortView> owner();

    HasValidable ipAddressValidator();
    HasValidable macAddressValidator();

    HasVisibility saveVisibility();

    HasEnabled saveEnabled();
}
