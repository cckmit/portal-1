package ru.protei.portal.ui.project.client.activity.table;

import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность таблицы проектов
 */
public interface AbstractProjectTableActivity
        extends ClickColumn.Handler<Project>, EditClickColumn.EditHandler<Project>, RemoveClickColumn.RemoveHandler<Project>
{
    void onEditClicked( Project value );
}
