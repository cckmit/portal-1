package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.view.EntityOption;
//import ru.protei.portal.core.model.view.EntityOption;

/**
 * Представление таблицы контактов
 */
public interface AbstractIssueTableView extends IsWidget {

    void setActivity( AbstractIssueTableActivity activity );
    HasValue<EntityOption> company();
    HasValue< En_SortField > sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    void resetFilter();
    void clearRecords();
    void addRecord( CaseObject issue );
}
