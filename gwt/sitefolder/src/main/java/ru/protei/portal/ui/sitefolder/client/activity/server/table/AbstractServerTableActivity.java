package ru.protei.portal.ui.sitefolder.client.activity.server.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.CopyClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractServerTableActivity extends
        ClickColumn.Handler<Server>, EditClickColumn.EditHandler<Server>, RemoveClickColumn.RemoveHandler<Server>, CopyClickColumn.CopyHandler<Server>,
        InfiniteLoadHandler<Server>, InfiniteTableWidget.PagerListener
{
    void onItemClicked(Server value);
    void onCopyClicked(Server value);
    void onEditClicked(Server value);
    void onRemoveClicked(Server value);
}