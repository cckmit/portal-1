package ru.protei.portal.ui.account.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление создания и редактирования учетной записи
 */
public interface AbstractAccountTableView extends IsWidget {
    void setActivity( AbstractAccountTableActivity activity );
    void setAnimation ( TableAnimation animation );

    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void setRecordCount( Long count );

    int getPageSize();

    int getPageCount();

    void scrollTo( int page );

    HasWidgets getPagerContainer();
}
