package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractAssignedIssuesTableActivity extends RemoveClickColumn.RemoveHandler<CaseShortView> {
    void onItemClicked(CaseShortView value);

    void onItemActionAssign(CaseShortView value, UIObject relative);
}
