package ru.protei.portal.ui.issue.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

/**
 * Абстракция вида фильтра обращений
 */
public interface AbstractIssueFilterView extends IsWidget {

    void setActivity( AbstractIssueFilterActivity activity );

    HasValue<EntityOption> company();
    HasValue<EntityOption> product();

    HasValue<En_CaseState> state();
    HasValue<En_ImportanceLevel> importance();

    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    void resetFilter();
}