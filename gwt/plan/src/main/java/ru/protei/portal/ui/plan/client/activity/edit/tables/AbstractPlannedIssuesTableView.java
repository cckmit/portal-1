package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;

import java.util.List;

public interface AbstractPlannedIssuesTableView extends IsWidget {
    void setActivity(AbstractPlannedIssuesTableActivity activity);

    void clearRecords();

    void putRecords(List<CaseShortView> list);

    void moveColumnVisibility(boolean isVisible);

    ClickColumnProvider<CaseShortView> getIssuesColumnProvider();
}
