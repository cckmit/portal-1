package ru.protei.portal.ui.issue.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractIssueTableActivity
        extends ClickColumn.Handler< CaseObject >, EditClickColumn.EditHandler< CaseObject >,
        InfiniteLoadHandler<CaseObject>, InfiniteTableWidget.PagerListener
{
    void onEditClicked( CaseObject value );
}