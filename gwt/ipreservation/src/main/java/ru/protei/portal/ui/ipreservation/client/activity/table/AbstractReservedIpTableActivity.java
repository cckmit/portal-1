package ru.protei.portal.ui.ipreservation.client.activity.table;

import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность таблицы зарезервированных IP
 */
public interface AbstractReservedIpTableActivity
        extends ClickColumn.Handler<ReservedIp>, EditClickColumn.EditHandler<ReservedIp>, RemoveClickColumn.RemoveHandler<ReservedIp>
{
    void onEditClicked( ReservedIp value );
}
