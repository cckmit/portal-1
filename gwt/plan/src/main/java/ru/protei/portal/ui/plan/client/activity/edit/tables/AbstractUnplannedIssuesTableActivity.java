package ru.protei.portal.ui.plan.client.activity.edit.tables;

import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;

public interface AbstractUnplannedIssuesTableActivity {
    void onItemClicked(CaseShortView value);

    void onItemActionAssign(CaseShortView value);

    void onFilterChanged(FilterShortView filter);

    void onIssueNumberChanged();
}
