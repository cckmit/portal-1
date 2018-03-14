package ru.protei.portal.ui.documentation.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

public interface AbstractDocumentationTableActivity
        extends ClickColumn.Handler<Documentation>, EditClickColumn.EditHandler<Documentation>,
        InfiniteLoadHandler<Documentation>, InfiniteTableWidget.PagerListener {
}