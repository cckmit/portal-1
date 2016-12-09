package ru.protei.portal.ui.crm.client.activity.dashboardblocks.table;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseObject;

import java.util.List;
import java.util.Set;

/**
 * Created by bondarenko on 01.12.16.
 */
public interface AbstractDashboardTableView extends IsWidget {

    void setActivity( AbstractDashboardTableActivity activity );
    void putRecords(List<CaseObject> cases);

    void setSectionName(String name);
    void setRecordsCount(int count);
    void showLoader(boolean isShow);
    HasValue<Set<En_ImportanceLevel>> getImportance();

}
