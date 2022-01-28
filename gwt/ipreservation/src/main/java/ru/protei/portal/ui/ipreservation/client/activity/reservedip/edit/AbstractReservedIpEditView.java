package ru.protei.portal.ui.ipreservation.client.activity.reservedip.edit;

import com.google.gwt.user.client.ui.*;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Абстракция вида карточки редактирования зарезервированного IP
 */
public interface AbstractReservedIpEditView extends IsWidget {

    void setActivity(AbstractReservedIpEditActivity activity);

    void setAddress(String value);
    HasValue<String> macAddress();
    HasValue<DateInterval> useRange();
    HasText comment();
    HasText lastActiveDate();
    HasText lastCheckInfo();
    HasValue<PersonShortView> owner();

    HasValidable macAddressValidator();

    HasVisibility saveVisibility();

    HasEnabled saveEnabled();
}
