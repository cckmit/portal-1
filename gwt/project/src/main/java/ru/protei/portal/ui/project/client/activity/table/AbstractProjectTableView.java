package ru.protei.portal.ui.project.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;

/**
 * Представление таблицы проектов
 */
public interface AbstractProjectTableView extends IsWidget {

    void setActivity( AbstractProjectTableActivity activity );
    void setAnimation( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();

    void addRows(List<Project> rows);

    void updateRow( Project project );

    void clearSelection();
}
