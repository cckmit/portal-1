package ru.protei.portal.ui.common.client.activity.ytwork.table;

import ru.protei.portal.core.model.ent.YoutrackReportDictionary;

public interface AbstractYoutrackReportDictionaryTableActivity {
    void onAddClicked();

    void onEditClicked(YoutrackReportDictionary value);
    
    void onRemoveClicked(YoutrackReportDictionary value);

    void onCollapseClicked(boolean isCollapsed);

    void onShow();
}
