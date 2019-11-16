package ru.protei.portal.app.portal.client.activity.dashboardblocks.table;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;
import java.util.Set;

/**
 * Created by bondarenko on 01.12.16.
 */
public interface AbstractDashboardTableView extends IsWidget {

    void setActivity( AbstractDashboardTableActivity activity );
    void putRecords(List<CaseShortView> cases);
    void clearRecords();

    void putCompanies(List<EntityOption> companies);

    void setSectionName(String name);
    void setRecordsCount(int count);
    void setFastOpenEnabled(boolean enabled);
    void showLoader(boolean isShow);
    HasValue<Set<En_ImportanceLevel>> getImportance();
    HasValue<String> getSearch();

    void toggleSearchIndicator(boolean show);
    void toggleInitiatorsIndicator(boolean show);

    void setEnsureDebugId(String debugId);
}
