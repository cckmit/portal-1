package ru.protei.portal.ui.ipreservation.client.activity.reservedip.create;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.ipreservation.client.view.widget.mode.En_ReservedMode;

import java.util.Set;

/**
 * Абстракция вида карточки резервирования IP адресов
 */
public interface AbstractReservedIpCreateView extends IsWidget {

    void setActivity(AbstractReservedIpCreateActivity activity);

    HasValue<En_ReservedMode> reservedMode();
    HasValue<String> ipAddress();
    HasValue<Long> number();
    HasValue<String> macAddress();
    HasText comment();
    HasValue<Set<SubnetOption>> subnets();
    HasValue<PersonShortView> owner();
    HasValue<DateIntervalWithType> useRange();

    HasValidable ipAddressValidator();
    HasValidable macAddressValidator();

    HasWidgets getExaсtIpContainer();
    HasWidgets getAnyFreeIpsContainer();

    HasVisibility exaсtIpVisibility();
    HasVisibility anyFreeIpsVisibility();

    HasVisibility saveVisibility();

    HasEnabled ownerEnabled();
    HasEnabled saveEnabled();
}
