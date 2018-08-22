package ru.protei.portal.ui.contact.client.activity.table.concise;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

/**
 * Представление таблицы контактов
 */
public interface AbstractContactConciseTableView extends IsWidget {

    void setActivity( AbstractContactConciseTableActivity activity );

    void clearRecords();

    void setData(List<Person> persons);

    void showEditableColumns(boolean isVisible);
}
