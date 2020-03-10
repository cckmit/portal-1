package ru.protei.portal.ui.ipreservation.client.activity.table;

import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RefreshClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность таблицы зарезервированных IP
 */
public interface AbstractReservedIpTableActivity  extends
        ClickColumn.Handler<ReservedIp>, EditClickColumn.EditHandler<ReservedIp>,
        RemoveClickColumn.RemoveHandler<ReservedIp>, RefreshClickColumn.RefreshHandler<ReservedIp> {

    //void onItemClicked( Subnet value );
    void onItemClicked( ReservedIp value );
    //void onEditClicked( Subnet value );
    void onEditClicked( ReservedIp value );
    //void oRemoveClicked( Subnet value );
    void oRemoveClicked( ReservedIp value );
    void oRefreshClicked( ReservedIp value );
}
