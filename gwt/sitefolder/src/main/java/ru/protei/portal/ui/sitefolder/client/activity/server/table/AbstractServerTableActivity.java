package ru.protei.portal.ui.sitefolder.client.activity.server.table;

import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.ui.common.client.columns.CopyClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.widget.table.HasGroupFunctions;

public interface AbstractServerTableActivity extends
        CopyClickColumn.CopyHandler<Server>,
        EditClickColumn.EditHandler<Server>,
        RemoveClickColumn.RemoveHandler<Server>,
        HasGroupFunctions<Server, ServerGroup> {

    void onCreateClicked();

    void onOpenAppsClicked(Server server);

    void onFilterChanged();
}
