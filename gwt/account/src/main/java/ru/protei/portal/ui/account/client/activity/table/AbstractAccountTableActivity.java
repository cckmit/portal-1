package ru.protei.portal.ui.account.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

/**
 * Активность создания и редактирования учетной записи
 */
public interface AbstractAccountTableActivity extends
        ClickColumn.Handler< UserLogin >, EditClickColumn.EditHandler< UserLogin >,
        InfiniteLoadHandler< UserLogin >, InfiniteTableWidget.PagerListener
{
}
