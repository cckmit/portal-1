package ru.protei.portal.ui.roomreservation.client.activity.table;

import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.widget.table.HasGroupFunctions;

import java.util.Date;

public interface AbstractRoomReservationTableActivity extends ClickColumn.Handler<RoomReservation>,
        EditClickColumn.EditHandler<RoomReservation>,
        RemoveClickColumn.RemoveHandler<RoomReservation>, HasGroupFunctions<RoomReservation, Date> {
    void onFilterChange();
}
