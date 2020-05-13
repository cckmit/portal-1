package ru.protei.portal.ui.project.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность таблицы проектов
 */
public interface AbstractProjectTableActivity
        extends ClickColumn.Handler<Project>, EditClickColumn.EditHandler<Project>, RemoveClickColumn.RemoveHandler<Project>,
        InfiniteLoadHandler<Project>, InfiniteTableWidget.PagerListener
{
    void onEditClicked( Project value );
}
