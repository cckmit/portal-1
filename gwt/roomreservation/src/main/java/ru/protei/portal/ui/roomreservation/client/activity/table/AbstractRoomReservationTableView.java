package ru.protei.portal.ui.roomreservation.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.roomreservation.client.widget.filter.RoomReservationParamWidget;

import java.util.List;

public interface AbstractRoomReservationTableView extends IsWidget {
    void setActivity(AbstractRoomReservationTableActivity activity);
    RoomReservationParamWidget getFilterWidget();
    void addRecords(List<RoomReservation> roomReservations);
    void clearRecords();
    HasWidgets getPagerContainer();
}
