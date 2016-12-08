package ru.protei.portal.ui.crm.client.activity.dashboard;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;

import java.util.Set;

/**
 * Created by bondarenko on 01.12.16.
 */
public interface AbstractDashboardView extends IsWidget {

    void setActivity( AbstractDashboardActivity activity );

    HasWidgets getActiveRecordsContainer();
    HasWidgets getNewRecordsContainer();
    HasWidgets getInactiveRecordsContainer();

    void setActiveRecordsCount(long count);
    void setNewRecordsCount(long count);
    void setInactiveRecordsCount(long count);

    HasValue<Set<En_ImportanceLevel>> getActiveRecordsImportance();
    HasValue<Set<En_ImportanceLevel>> getNewRecordsImportance();
    HasValue<Set<En_ImportanceLevel>> getInactiveRecordsImportance();

    void showInactiveRecordsLoader(boolean isShow);

}
