package ru.protei.portal.ui.contact.client.activity.table;

import ru.brainworm.factory.widget.table.client.helper.ClickColumn;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.contact.client.view.table.columns.EditClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractContactTableActivity extends ClickColumn.Handler< Person >, EditClickColumn.EditHandler< Person > {

    void onFilterChanged();

}
