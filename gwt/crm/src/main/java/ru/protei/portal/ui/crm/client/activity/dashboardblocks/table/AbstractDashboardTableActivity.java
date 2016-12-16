package ru.protei.portal.ui.crm.client.activity.dashboardblocks.table;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;

import java.util.Set;

/**
 * Активность таблицы контактов
 */
public interface AbstractDashboardTableActivity extends ClickColumn.Handler<CaseShortView>{
    void updateImportance(AbstractDashboardTableView view, Set<En_ImportanceLevel> importanceLevels);
    void removeView(AbstractDashboardTableView view);
}