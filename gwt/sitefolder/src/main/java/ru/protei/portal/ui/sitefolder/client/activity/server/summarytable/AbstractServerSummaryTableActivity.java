package ru.protei.portal.ui.sitefolder.client.activity.server.summarytable;

import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.CopyClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.widget.table.HasGroupFunctions;

public interface AbstractServerSummaryTableActivity extends
        ClickColumn.Handler<Server>, EditClickColumn.EditHandler<Server>,
        RemoveClickColumn.RemoveHandler<Server>, CopyClickColumn.CopyHandler<Server>,
        HasGroupFunctions<Server, ServerGroup>
{
    void onItemClicked(Server value);
    void onCopyClicked(Server value);
    void onEditClicked(Server value);
    void onRemoveClicked(Server value);
    void onOpenAppsClicked(Server value);
}
