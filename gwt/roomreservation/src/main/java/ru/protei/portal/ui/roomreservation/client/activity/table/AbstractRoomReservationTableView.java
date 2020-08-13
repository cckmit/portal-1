package ru.protei.portal.ui.roomreservation.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.roomreservation.client.widget.filter.RoomReservationParamWidget;

public interface AbstractRoomReservationTableView extends IsWidget {
    void setActivity(AbstractRoomReservationTableActivity activity);
    RoomReservationParamWidget getFilterParam();
    void clearRecords();
    HasWidgets getPagerContainer();
    void setTotalRecords(int totalRecords);
    int getPageCount();
    void scrollTo(int page);
    void triggerTableLoad();
}