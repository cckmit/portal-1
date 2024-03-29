package ru.protei.portal.ui.roomreservation.client.widget.selector.reason;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RoomReservationReason;
import ru.protei.portal.ui.common.client.lang.En_RoomReservationReasonLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

import static java.util.Arrays.asList;
import static ru.protei.portal.core.model.dict.En_RoomReservationReason.*;

public class RoomReservationReasonButtonSelector extends ButtonSelector<En_RoomReservationReason> {

    @Inject
    public void init() {
        setDisplayOptionCreator(o -> new DisplayOption(o == null ? defaultValue : lang.getName(o)));
        fillOptions(makeOptionsByCustomOrder());
    }

    private List<En_RoomReservationReason> makeOptionsByCustomOrder() {
        return asList(NEGOTIATION, MEETING, PRESENTATION, EDUCATION, INTERVIEW, OTHER);
    }

    public void fillOptions(List<En_RoomReservationReason> items) {
        clearOptions();
        items.forEach(this::addOption);
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    @Inject
    private En_RoomReservationReasonLang lang;

    private String defaultValue;
}
