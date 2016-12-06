package ru.protei.portal.ui.crm.client.activity.dashboard;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;

import java.util.Set;

/**
 * Created by bondarenko on 01.12.16.
 */
public interface AbstractDashboardActivity {

    void updateActiveRecordsImportance(Set<En_ImportanceLevel> importanceLevels);
    void updateNewRecordsImportance(Set<En_ImportanceLevel> importanceLevels);
    void updateInactiveRecordsImportance(Set<En_ImportanceLevel> importanceLevels);

}
