package ru.protei.portal.ui.common.client.activity.ytwork;

import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackWorkDictionaryTableView;

public interface AbstractYoutrackWorkFilterActivity {
    AbstractYoutrackWorkDictionaryTableView getDictionaryTable(En_YoutrackWorkType type);
    
    void onFilterChanged();
}
