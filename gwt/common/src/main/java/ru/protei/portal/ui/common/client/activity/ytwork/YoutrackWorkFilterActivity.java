package ru.protei.portal.ui.common.client.activity.ytwork;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackWorkDictionaryTableActivity;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackWorkDictionaryTableView;
import ru.protei.portal.ui.common.client.lang.YoutrackWorkLang;

public abstract class YoutrackWorkFilterActivity implements AbstractYoutrackWorkFilterActivity, Activity {

    @Override
    public AbstractYoutrackWorkDictionaryTableView getDictionaryTable(En_YoutrackWorkType type) {
        AbstractYoutrackWorkDictionaryTableView table = tableViewProvider.get();
        table.setEnsureDebugId(DebugIds.YOUTRACK_WORK.TABLE + type.getId());
        table.setName(youtrackWorkLang.getTypeName(type));
        table.setCollapsed(true);
        AbstractYoutrackWorkDictionaryTableActivity activity = tableActivityProvider.get();
        activity.setTypeAndTable(type, table);
        table.setActivity(activity);
        return table;
    }

    @Override
    public void onFilterChanged() {
        // ничего не делаем
    }

    @Inject
    YoutrackWorkLang youtrackWorkLang;
    @Inject
    Provider<AbstractYoutrackWorkDictionaryTableView> tableViewProvider;
    @Inject
    Provider<AbstractYoutrackWorkDictionaryTableActivity> tableActivityProvider;
}