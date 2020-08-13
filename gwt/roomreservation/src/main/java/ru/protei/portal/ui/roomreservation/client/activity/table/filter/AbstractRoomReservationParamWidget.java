package ru.protei.portal.ui.roomreservation.client.activity.table.filter;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterParamView;

public interface AbstractRoomReservationParamWidget extends IsWidget, FilterParamView<RoomReservationQuery> {
    boolean isValidDateRange();
}