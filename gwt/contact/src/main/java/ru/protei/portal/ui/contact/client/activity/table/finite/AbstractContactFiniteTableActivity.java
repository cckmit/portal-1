package ru.protei.portal.ui.contact.client.activity.table.finite;

import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractContactFiniteTableActivity extends ClickColumn.Handler<Person>, EditClickColumn.EditHandler<Person>, RemoveClickColumn.RemoveHandler<Person> {
    void onItemClicked(Person value);
    void onEditClicked(Person value);
    void onRemoveClicked(Person value);
}
