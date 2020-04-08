package ru.protei.portal.ui.project.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

/**
 * Абстракция вида фильтра проектов
 */
public interface AbstractProjectFilterView extends IsWidget {

    void setActivity( AbstractProjectFilterActivity activity );

    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    void resetFilter();

    HasValue< Set< En_RegionState > > states();

    HasValue<Set<ProductDirectionInfo>> direction();

    HasValue<Boolean> onlyMineProjects();

    HasValue< Set<EntityOption> > regions();

    HasValue< Set<PersonShortView> > headManagers();

    HasValue< Set<PersonShortView> > caseMembers();
}