package ru.protei.portal.ui.common.client.activity.ytwork.table;

import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;

public interface AbstractYoutrackWorkDictionaryTableActivity {
    void setTypeAndTable(En_YoutrackWorkType type, AbstractYoutrackWorkDictionaryTableView table);

    void onAddClicked();

    void onEditClicked(YoutrackWorkDictionary value);
    
    void onRemoveClicked(YoutrackWorkDictionary value);

    void onCollapseClicked(boolean isCollapsed);

    void refreshTable();

    void onSearchChanged();
}
