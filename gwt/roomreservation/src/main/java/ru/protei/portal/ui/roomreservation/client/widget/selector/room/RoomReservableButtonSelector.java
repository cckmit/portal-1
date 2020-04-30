package ru.protei.portal.ui.roomreservation.client.widget.selector.room;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class RoomReservableButtonSelector extends ButtonPopupSingleSelector<RoomReservable> {

    @Inject
    void init(RoomReservableModel model) {
        setAsyncModel(model);
        setSearchEnabled(false);
        setItemRenderer(this::makeItemView);
    }

    private String makeItemView(RoomReservable room) {
        return room == null ? defaultValue : room.getName();
    }
}
