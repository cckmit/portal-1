package ru.protei.portal.ui.common.client.activity.projectfilter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;

import java.util.Set;

/**
 * Абстракция вида фильтра проектов
 */
public interface AbstractProjectFilterView extends IsWidget {

    void setActivity( AbstractProjectFilterActivity activity );

    HasValue<En_SortField> sortField();

    HasValue< Boolean > sortDir();

    HasValue< String > searchPattern();

    HasValue< Set< CaseState> > states();

    HasValue<Set<ProductDirectionInfo>> direction();

    HasValue<Boolean> onlyMineProjects();

    HasValue< Set<EntityOption> > regions();

    HasValue< Set<PersonShortView> > headManagers();

    HasValue< Set<PersonShortView> > caseMembers();

    HasValue< Set<EntityOption> > initiatorCompanies();

    HasValue<DateIntervalWithType> commentCreationRange();

    boolean isCommentCreationRangeTypeValid();

    boolean isCommentCreationRangeValid();

    void setCommentCreationRangeValid(boolean isTypeValid, boolean isRangeValid);

    HasVisibility onlyMineProjectsVisibility();

    void resetFilter();
    void clearFooterStyle();
}