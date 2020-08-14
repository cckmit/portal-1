package ru.protei.portal.ui.roomreservation.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractRoomReservationTableActivity extends ClickColumn.Handler<RoomReservation>,
        EditClickColumn.EditHandler<RoomReservation>,
        RemoveClickColumn.RemoveHandler<RoomReservation> {
    void onFilterChange();
}