package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

public interface AbstractUnplannedIssuesTableView extends IsWidget {
    void setActivity(AbstractUnplannedIssuesTableActivity activity);

    void clearRecords();

    void putRecords(List<CaseShortView> list);

    void setTotalRecords(int totalRecords);

    void updateFilterSelector();

    HasValue<CaseFilterShortView> filter();

    void setLimitLabel(String value);
}
