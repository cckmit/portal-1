package ru.protei.portal.ui.issueassignment.client.activity.table;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;

public interface AbstractTableActivity {

    void onItemClicked(CaseShortView value);

    void onItemActionAssign(CaseShortView value, UIObject relative);

    void onFilterChanged(CaseFilterShortView filter);
}
