package ru.protei.portal.ui.issue.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.AttachClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractIssueTableActivity
        extends ClickColumn.Handler< CaseShortView >, EditClickColumn.EditHandler< CaseShortView >,
        InfiniteLoadHandler<CaseShortView>, InfiniteTableWidget.PagerListener, AttachClickColumn.AttachHandler<CaseShortView>
{
    void onEditClicked( CaseShortView value );
}
