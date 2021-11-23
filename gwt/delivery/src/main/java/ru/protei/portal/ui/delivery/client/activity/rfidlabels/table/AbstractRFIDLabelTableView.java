package ru.protei.portal.ui.delivery.client.activity.rfidlabels.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.RFIDLabel;

public interface AbstractRFIDLabelTableView extends IsWidget {
    void setActivity(AbstractRFIDLabelTableActivity tableActivity);

    void clearRecords();
    void triggerTableLoad();
    void setTotalRecords(int totalRecords);
    int getPageCount();
    void scrollTo(int page);

    HasWidgets getFilterContainer();
    HasWidgets getPagerContainer();

    void updateRow(RFIDLabel item);
}
