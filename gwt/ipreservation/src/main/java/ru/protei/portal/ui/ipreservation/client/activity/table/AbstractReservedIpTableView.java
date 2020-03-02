package ru.protei.portal.ui.ipreservation.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление таблицы заразервированных IP
 */
public interface AbstractReservedIpTableView extends IsWidget {

    void setActivity( AbstractReservedIpTableActivity activity );
    void setAnimation( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();

    void addSeparator( String text );
    void clearSelection();
}
