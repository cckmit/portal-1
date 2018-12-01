package ru.protei.portal.ui.project.client.activity.table;

import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractProjectTableActivity
        extends ClickColumn.Handler<ProjectInfo>, EditClickColumn.EditHandler< ProjectInfo >, RemoveClickColumn.RemoveHandler< ProjectInfo >
{
//    void onEditClicked( ProjectInfo value );
}
