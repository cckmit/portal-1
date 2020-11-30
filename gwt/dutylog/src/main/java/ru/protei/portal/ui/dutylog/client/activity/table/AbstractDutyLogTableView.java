package ru.protei.portal.ui.dutylog.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.ui.dutylog.client.widget.filter.DutyLogFilterWidget;

import java.util.List;

public interface AbstractDutyLogTableView extends IsWidget {
    void setActivity(AbstractDutyLogTableActivity activity);
    void clearRecords();

    HasWidgets getPagerContainer();

    void addRecords(List<DutyLog> dutyLogs);

    DutyLogFilterWidget getFilterWidget();
}
