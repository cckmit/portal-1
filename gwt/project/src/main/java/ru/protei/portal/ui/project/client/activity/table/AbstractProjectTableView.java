package ru.protei.portal.ui.project.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление таблицы проектов
 */
public interface AbstractProjectTableView extends IsWidget {

    void setActivity( AbstractProjectTableActivity activity );
    void setAnimation( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();

    void addRow( Project row );

    void addSeparator( String text );

    void updateRow( Project project );

    void clearSelection();
}
