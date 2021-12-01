package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;

public interface AbstractUnplannedIssuesTableView extends IsWidget {
    void setActivity(AbstractUnplannedIssuesTableActivity activity);

    void clearRecords();

    void putRecords(List<CaseShortView> list);

    void setTotalRecords(int totalRecords);

    void updateFilterSelector();

    HasValue<FilterShortView> filter();

    HasValue<String> issueNumber();

    HasValidable issueNumberValidator();

    void setLimitLabel(String value);

    void setIssueDefaultCursor(boolean isDefault);

    ClickColumnProvider<CaseShortView> getIssuesColumnProvider();
}
