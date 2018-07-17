package ru.protei.portal.ui.sitefolder.client.activity.app.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractSiteFolderAppTableActivity extends
        ClickColumn.Handler<Application>, EditClickColumn.EditHandler<Application>, RemoveClickColumn.RemoveHandler<Application>,
        InfiniteLoadHandler<Application>, InfiniteTableWidget.PagerListener
{
    void onItemClicked(Application value);
    void onEditClicked(Application value);
    void onRemoveClicked(Application value);
}
