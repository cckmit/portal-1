package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.plan.client.view.columns.DragColumn;

public interface AbstractPlannedIssuesTableActivity extends RemoveClickColumn.RemoveHandler<CaseShortView>, DragColumn.Handler< CaseShortView > {
    void onItemClicked(CaseShortView value);

    void onItemActionAssign(CaseShortView value, UIObject relative);
}
