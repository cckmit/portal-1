package ru.protei.portal.ui.contact.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractContactTableActivity
        extends ClickColumn.Handler< Person >, EditClickColumn.EditHandler< Person >, RemoveClickColumn.RemoveHandler<Person>,
        InfiniteLoadHandler< Person >, InfiniteTableWidget.PagerListener
{
    void onEditClicked(Person value );
}
