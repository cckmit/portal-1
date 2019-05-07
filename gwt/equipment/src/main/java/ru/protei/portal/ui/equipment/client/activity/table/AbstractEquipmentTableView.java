package ru.protei.portal.ui.equipment.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление таблицы контактов
 */
public interface AbstractEquipmentTableView extends IsWidget {

    void setActivity( AbstractEquipmentTableActivity activity );

    void setAnimation ( TableAnimation animation );

    void clearRecords();

    void clearSelection();

    HasWidgets getPreviewContainer ();

    HasWidgets getFilterContainer ();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo( int page );

    HasWidgets getPagerContainer();
}
