package ru.protei.portal.ui.common.client.activity.ytwork;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackReportDictionaryTableView;
import ru.protei.portal.ui.common.client.activity.ytwork.table.YoutrackReportDictionaryTableActivity;
import ru.protei.portal.ui.common.client.lang.ReportYoutrackWorkLang;

public abstract class YtWorkFilterActivity implements AbstractYtWorkFilterActivity, Activity {

    @Override
    public AbstractYoutrackReportDictionaryTableView getDictionaryTable(En_ReportYoutrackWorkType type) {
        AbstractYoutrackReportDictionaryTableView table = tableProvider.get();
        table.setEnsureDebugId(DebugIds.YOUTRACK_WORK_REPORT.TABLE + type.getId());
        table.setName(reportYoutrackWorkLang.getTypeName(type));
        table.setCollapsed(true);
        YoutrackReportDictionaryTableActivity activity = tableActivityProvider.get();
        activity.setTypeAndTable(type, table);
        table.setActivity(activity);
        return table;
    }

    @Override
    public void onFilterChanged() {
        // ничего не делаем
    }

    @Inject
    ReportYoutrackWorkLang reportYoutrackWorkLang;
    @Inject
    Provider<AbstractYoutrackReportDictionaryTableView> tableProvider;
    @Inject
    Provider<YoutrackReportDictionaryTableActivity> tableActivityProvider;
}