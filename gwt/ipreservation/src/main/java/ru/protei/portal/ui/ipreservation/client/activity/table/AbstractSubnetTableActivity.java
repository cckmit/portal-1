package ru.protei.portal.ui.ipreservation.client.activity.table;

import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RefreshClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность таблицы подсетей
 */
public interface AbstractSubnetTableActivity  extends
        ClickColumn.Handler<Subnet>, EditClickColumn.EditHandler<Subnet>,
        RemoveClickColumn.RemoveHandler<Subnet>, RefreshClickColumn.RefreshHandler<Subnet>{
}
