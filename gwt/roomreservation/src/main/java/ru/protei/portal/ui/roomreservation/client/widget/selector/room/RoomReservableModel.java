package ru.protei.portal.ui.roomreservation.client.widget.selector.room;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.RoomReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class RoomReservableModel extends BaseSelectorModel<RoomReservable> implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        roomReservationController.getRooms(new FluentCallback<List<RoomReservable>>()
                .withErrorMessage(lang.errGetList())
                .withSuccess(result -> updateElements(result, selector)));
    }


    @Inject
    Lang lang;
    @Inject
    RoomReservationControllerAsync roomReservationController;
}
