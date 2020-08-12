package ru.protei.portal.app.portal.client.activity.dashboardblocks.table;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

public interface AbstractDashboardTableView extends IsWidget {

    void setActivity(AbstractDashboardTableActivity activity);

    void clearRecords();

    void putRecords(List<CaseShortView> list);

    void setName(String name);

    void setCollapsed(boolean isCollapsed);

    void setTotalRecords(int totalRecords);

    void showLoader(boolean isShow);

    void showTableOverflow(int showedRecords);

    void hideTableOverflow();

    void setEnsureDebugId(String debugId);
}
