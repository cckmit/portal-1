package ru.protei.portal.ui.roomreservation.client.widget.selector.room;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class RoomReservationMultiSelector extends InputPopupMultiSelector<RoomReservable> {

    @Inject
    void init(RoomReservableModel model, Lang lang) {
        setAsyncModel(model);
        setSearchEnabled(false);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(room -> room == null ? lang.selectValue() : room.getName());
    }
}
