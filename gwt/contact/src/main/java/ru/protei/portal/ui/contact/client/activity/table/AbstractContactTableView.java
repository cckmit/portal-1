package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;

/**
 * Представление таблицы контактов
 */
public interface AbstractContactTableView extends IsWidget {

    void setActivity( AbstractContactTableActivity activity );
    void setAnimation ( TableAnimation animation );

    void hideElements();
    void showElements();

    void addRecords( List< Person > persons );
    void clearRecords();

    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();
    HasWidgets getPagerContainer();

    void clearSelection();

    void scrollTo(int page);

    void triggerTableLoad();

    int getPageCount();

    void setTotalRecords(int totalRecords);
}
