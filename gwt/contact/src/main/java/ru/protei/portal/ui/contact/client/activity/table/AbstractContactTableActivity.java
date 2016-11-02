package ru.protei.portal.ui.contact.client.activity.table;

import ru.brainworm.factory.widget.table.client.helper.ClickColumn;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.contact.client.view.table.columns.ActionColumn;

/**
 * Created by turik on 28.10.16.
 */
public interface AbstractContactTableActivity extends ClickColumn.Handler< Person >, ActionColumn.ActionHandler< Person > {

    void onFilterChanged();
    void onEditClick(Person value );
    void onCreateClick ();
}
