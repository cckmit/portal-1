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

    void updateRow( Project project );

    void clearSelection();

    HasWidgets getPagerContainer();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo( int page );
}
