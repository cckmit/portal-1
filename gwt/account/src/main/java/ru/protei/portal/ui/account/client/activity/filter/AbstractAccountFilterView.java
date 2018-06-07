package ru.protei.portal.ui.account.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

/**
 * Абстракция вида фильтра учетных записей
 */
public interface AbstractAccountFilterView extends IsWidget {

    void setActivity( AbstractAccountFilterActivity activity );

    HasValue< String > searchPattern();
    HasValue< En_SortField > sortField();
    HasValue< Boolean > sortDir();
    HasValue< Set< En_AuthType > > types();
    HasValue<EntityOption> company();
    HasValue< Set< UserRole > > roles();

    void resetFilter();
}
