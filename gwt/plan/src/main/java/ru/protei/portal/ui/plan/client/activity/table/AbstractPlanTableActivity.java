package ru.protei.portal.ui.plan.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractPlanTableActivity extends
        ClickColumn.Handler<Plan>, EditClickColumn.EditHandler<Plan>, RemoveClickColumn.RemoveHandler<Plan>,
        InfiniteLoadHandler<Plan>, InfiniteTableWidget.PagerListener{
}
