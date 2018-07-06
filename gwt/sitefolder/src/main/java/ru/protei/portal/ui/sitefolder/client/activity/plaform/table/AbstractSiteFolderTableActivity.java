package ru.protei.portal.ui.sitefolder.client.activity.plaform.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractSiteFolderTableActivity extends
        ClickColumn.Handler<Platform>, EditClickColumn.EditHandler<Platform>, RemoveClickColumn.RemoveHandler<Platform>,
        InfiniteLoadHandler<Platform>, InfiniteTableWidget.PagerListener
{
    void onItemClicked(Platform value);
    void onEditClicked(Platform value);
    void onRemoveClicked(Platform value);
}
