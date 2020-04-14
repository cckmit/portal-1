package ru.protei.portal.ui.ipreservation.client.activity.reservedip.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RefreshClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность таблицы зарезервированных IP
 */
public interface AbstractReservedIpTableActivity  extends
        ClickColumn.Handler<ReservedIp>, EditClickColumn.EditHandler<ReservedIp>,
        RemoveClickColumn.RemoveHandler<ReservedIp>, RefreshClickColumn.RefreshHandler<ReservedIp>,
        InfiniteLoadHandler<ReservedIp>, InfiniteTableWidget.PagerListener {
}
