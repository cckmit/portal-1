package ru.protei.portal.ui.ipreservation.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;

/**
 * Представление таблицы заразервированных IP
 */
public interface AbstractReservedIpTableView extends IsWidget {
    void setActivity(AbstractReservedIpTableActivity activity);
    void setAnimation(TableAnimation animation);

    void addRow( ReservedIp reservedIps );
    void updateRow(ReservedIp project);
    void clearRecords();

    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();
    HasWidgets getPagerContainer();

    void clearSelection();
}
