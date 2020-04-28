package ru.protei.portal.ui.roomreservation.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_RoomReservationReason;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.roomreservation.client.widget.datetime.HasVaryAbility;

import java.util.List;
import java.util.Set;

public interface AbstractRoomReservationEditView extends IsWidget {

    void setActivity(AbstractRoomReservationEditActivity activity);

    HasVisibility loadingVisibility();

    HasVisibility contentVisibility();

    void fillCoffeeBreakCountOptions(List<String> options);

    void setRoomAccessibilityMessage(boolean isAccessible, String message);

    HasValue<PersonShortView> personResponsible();

    HasValue<RoomReservable> room();

    HasValue<En_RoomReservationReason> reason();

    HasValue<String> coffeeBreakCount();

    HasValue<List<DateInterval>> dates();

    HasValue<Set<PersonShortView>> notifiers();

    HasValue<String> comment();

    HasEnabled personResponsibleEnabled();

    HasEnabled roomEnabled();

    HasEnabled reasonEnabled();

    HasEnabled coffeeBreakCountEnabled();

    HasEnabled datesEnabled();

    HasEnabled notifiersEnabled();

    HasEnabled commentEnabled();

    HasVaryAbility datesVaryAbility();
}
