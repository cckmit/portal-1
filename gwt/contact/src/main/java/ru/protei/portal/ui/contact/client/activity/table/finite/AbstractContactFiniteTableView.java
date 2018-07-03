package ru.protei.portal.ui.contact.client.activity.table.finite;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

/**
 * Представление таблицы контактов
 */
public interface AbstractContactFiniteTableView extends IsWidget {

    void setActivity( AbstractContactFiniteTableActivity activity );

    void clearRecords();

    void setData(List<Person> persons);
}
