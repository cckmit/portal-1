package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление таблицы контактов
 */
public interface AbstractContactTableView extends IsWidget {

    void setActivity( AbstractContactTableActivity activity );
    void setAnimation ( TableAnimation animation );

    HasValue<EntityOption> company();
    HasValue< Boolean > showFired();
    HasValue< En_SortField > sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    void resetFilter();
    void hideElements();
    void showElements();
    void clearRecords();
    HasWidgets getPreviewContainer ();

    void setRecordCount( Long count );
}
