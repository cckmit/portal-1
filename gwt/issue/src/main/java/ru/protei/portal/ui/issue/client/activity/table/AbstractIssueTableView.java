package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление таблицы контактов
 */
public interface AbstractIssueTableView extends IsWidget {

    void setActivity( AbstractIssueTableActivity activity );
    void setAnimation ( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo( int page );

    void updateRow(CaseShortView item);

    void hideElements();
    void showElements();

    HasWidgets getPagerContainer();

    void clearSelection();
}
