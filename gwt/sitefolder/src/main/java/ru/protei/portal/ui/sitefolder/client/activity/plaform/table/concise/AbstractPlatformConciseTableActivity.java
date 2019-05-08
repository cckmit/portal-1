package ru.protei.portal.ui.sitefolder.client.activity.plaform.table.concise;

import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractPlatformConciseTableActivity extends ClickColumn.Handler<Platform>, EditClickColumn.EditHandler<Platform>, RemoveClickColumn.RemoveHandler<Platform> {
    void onItemClicked(Platform value);
    void onEditClicked(Platform value);
    void onRemoveClicked(Platform value);
}
