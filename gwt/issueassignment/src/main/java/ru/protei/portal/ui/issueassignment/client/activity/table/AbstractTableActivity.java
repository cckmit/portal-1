package ru.protei.portal.ui.issueassignment.client.activity.table;

import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;

public interface AbstractTableActivity {

    void onItemClicked(CaseShortView value);

    void onItemActionAssign(CaseShortView value);

    void onFilterChanged(CaseFilterShortView filter);
}
