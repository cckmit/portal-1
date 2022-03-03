package ru.protei.portal.app.portal.client.activity.dashboardblocks.table;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.events.Draggable;

import java.util.List;
import java.util.function.Predicate;

public interface AbstractDashboardIssueTableView extends IsWidget, Draggable {

    void setActivity(AbstractDashboardIssueTableActivity activity);

    void clearRecords();

    void putRecords(List<CaseShortView> list);

    void setName(String name);

    void setCollapsed(boolean isCollapsed);

    void setTotalRecords(int totalRecords);

    void showLoader(boolean isShow);

    void showTableOverflow(int showedRecords);

    void hideTableOverflow();

    void setEnsureDebugId(String debugId);

    void setChangeSelectionIfSelectedPredicate(Predicate<CaseShortView> changeSelectionIfSelectedPredicate);
}
