package ru.protei.portal.ui.role.client.activity.table;

import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

/**
 * Активность таблицы роли
 */
public interface AbstractRoleTableActivity
        extends ClickColumn.Handler< UserRole >, EditClickColumn.EditHandler< UserRole >
{
}
