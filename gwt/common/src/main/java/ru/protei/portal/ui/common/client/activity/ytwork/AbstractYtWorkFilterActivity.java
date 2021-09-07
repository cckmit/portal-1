package ru.protei.portal.ui.common.client.activity.ytwork;

import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackReportDictionaryTableView;

public interface AbstractYtWorkFilterActivity {
    AbstractYoutrackReportDictionaryTableView getDictionaryTable(En_ReportYoutrackWorkType type);
    
    void onFilterChanged();
}
