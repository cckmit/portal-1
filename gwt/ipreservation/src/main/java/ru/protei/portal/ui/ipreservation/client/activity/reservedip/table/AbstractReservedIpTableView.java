package ru.protei.portal.ui.ipreservation.client.activity.reservedip.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление таблицы заразервированных IP
 */
public interface AbstractReservedIpTableView extends IsWidget {
    void setActivity(AbstractReservedIpTableActivity activity);
    void setAnimation(TableAnimation animation);

    void clearRecords();
    void triggerTableLoad();
    void setTotalRecords(int totalRecords);
    int getPageCount();
    void scrollTo(int page);

    void updateRow(ReservedIp item);

    void hideElements();
    void showElements();

    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();
    HasWidgets getPagerContainer();

    void clearSelection();
}
