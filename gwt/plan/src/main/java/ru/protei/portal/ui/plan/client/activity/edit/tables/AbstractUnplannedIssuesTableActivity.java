package ru.protei.portal.ui.plan.client.activity.edit.tables;

import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;

public interface AbstractUnplannedIssuesTableActivity {
    void onItemClicked(CaseShortView value);

    void onItemActionAssign(CaseShortView value);

    void onFilterChanged(CaseFilterShortView filter);
}
