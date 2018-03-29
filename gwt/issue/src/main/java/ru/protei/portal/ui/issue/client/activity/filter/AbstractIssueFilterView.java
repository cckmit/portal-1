package ru.protei.portal.ui.issue.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.IssueFilterShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import java.util.Set;

/**
 * Абстракция вида фильтра обращений
 */
public interface AbstractIssueFilterView extends IsWidget {

    void setActivity( AbstractIssueFilterActivity activity );

    HasValue<Set<EntityOption>> companies();
    HasValue<Set<ProductShortView>> products();
    HasValue<Set<PersonShortView>> managers();
    HasValue<Set<En_CaseState>> states();
    HasValue<Set<En_ImportanceLevel>> importances();
    HasValue<DateInterval> dateRange ();
    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    void resetFilter();

    HasVisibility companiesVisibility();
    HasVisibility productsVisibility();
    HasVisibility managersVisibility();

    HasValue<IssueFilterShortView > userFilter();

    HasVisibility removeFilterBtnVisibility();

    HasValue<String> filterName();

    void setFilterNameContainerErrorStyle( boolean hasError );

    void setFilterNameContainerVisibility( boolean isVisible );

    void setCompaniesErrorStyle( boolean hasError );

    void setProductsErrorStyle( boolean hasError );

    void setManagersErrorStyle( boolean hasError );
}