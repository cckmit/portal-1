package ru.protei.portal.ui.ipreservation.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление таблицы подсетей
 */
public interface AbstractSubnetTableView extends IsWidget {
    void setActivity(AbstractSubnetTableActivity activity);
    void setAnimation(TableAnimation animation);

    void addRow( Subnet subnet );
    void updateRow( Subnet subnet);
    void clearRecords();

/*    void hideElements();
    void showElements();*/

    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();

    void clearSelection();
}
