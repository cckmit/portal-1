package ru.protei.portal.ui.contact.client.activity.table;

import ru.brainworm.factory.widget.table.client.helper.ClickColumn;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.contact.client.view.table.columns.EditActionClickColumn;
import ru.protei.portal.ui.contact.client.view.table.columns.EditClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractContactTableActivity extends ClickColumn.Handler< Person >, EditActionClickColumn.Handler < Person >, EditActionClickColumn.EditHandler< Person > {

    void onFilterChanged();
    void onEditClick(Person value );
    void onCreateClick ();
}
