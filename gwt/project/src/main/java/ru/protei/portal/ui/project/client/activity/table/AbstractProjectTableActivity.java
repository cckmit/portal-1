package ru.protei.portal.ui.project.client.activity.table;

import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractProjectTableActivity
        extends ClickColumn.Handler<ProjectInfo>, EditClickColumn.EditHandler< ProjectInfo >
{
//    void onEditClicked( ProjectInfo value );
}
