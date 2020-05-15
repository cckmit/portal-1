package ru.protei.portal.ui.account.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность создания и редактирования учетной записи
 */
public interface AbstractAccountTableActivity extends
        ClickColumn.Handler< UserLogin >,
        EditClickColumn.EditHandler< UserLogin >, RemoveClickColumn.RemoveHandler< UserLogin >,
        InfiniteLoadHandler<UserLogin>, InfiniteTableWidget.PagerListener
{
}
