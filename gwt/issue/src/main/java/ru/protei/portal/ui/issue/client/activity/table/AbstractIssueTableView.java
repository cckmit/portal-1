package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Представление таблицы контактов
 */
public interface AbstractIssueTableView extends IsWidget {

    void setActivity( AbstractIssueTableActivity activity );
    void setAnimation ( TableAnimation animation );

    void clearRecords();
    void addRecord( CaseObject issue );
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();
}
