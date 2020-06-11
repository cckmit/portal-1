package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;

public interface AbstractUnassignedIssuesTableActivity {
    void onItemClicked(CaseShortView value);

    void onItemActionAssign(CaseShortView value, UIObject relative);

    void onFilterChanged(CaseFilterShortView filter);
}
