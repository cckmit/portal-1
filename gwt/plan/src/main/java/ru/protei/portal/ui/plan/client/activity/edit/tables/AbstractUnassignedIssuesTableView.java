package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

public interface AbstractUnassignedIssuesTableView extends IsWidget {
    void setActivity(AbstractUnassignedIssuesTableActivity activity);

    void clearRecords();

    void putRecords(List<CaseShortView> list);

    void setTotalRecords(int totalRecords);

    void showLoader(boolean isShow);

    void updateFilterSelector();

    HasValue<CaseFilterShortView> filter();
}
