package ru.protei.portal.ui.issueassignment.client.activity.table;

import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.FilterShortView;

public interface AbstractTableActivity {

    void onItemClicked(CaseShortView value);

    void onItemActionAssign(CaseShortView value, UIObject relative);

    void onFilterChanged(FilterShortView filter);
}
