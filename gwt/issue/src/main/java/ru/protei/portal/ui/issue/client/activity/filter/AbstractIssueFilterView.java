package ru.protei.portal.ui.issue.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterActivity;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Абстракция вида фильтра обращений
 */
public interface AbstractIssueFilterView extends IsWidget {

    void setActivity( AbstractIssueFilterActivity activity, IssueFilterActivity issueFilterActivity );

    HasValue<Set<EntityOption>> companies();
    HasValue<Set<ProductShortView>> products();
    HasValue<Set<PersonShortView>> managers();
    HasValue<Set<PersonShortView>> initiators();
    HasValue<Set<En_CaseState>> states();
    HasValue<Set<En_ImportanceLevel>> importances();
    HasValue<DateInterval> dateRange ();
    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    HasValue<Boolean> searchByComments();
    HasValue<Boolean> searchPrivate();
    void resetFilter();
    void fillFilterFields(CaseQuery caseQuery);

    HasVisibility companiesVisibility();
    HasVisibility productsVisibility();
    HasVisibility managersVisibility();
    HasVisibility searchPrivateVisibility();

    HasValue<CaseFilterShortView > userFilter();

    void changeUserFilterValueName( CaseFilterShortView value );

    void addUserFilterDisplayOption( CaseFilterShortView value );

    HasVisibility removeFilterBtnVisibility();

    void setSaveBtnLabel( String name );

    HasValue<String> filterName();

    void setFilterNameContainerErrorStyle( boolean hasError );

    void setUserFilterNameVisibility( boolean hasVisible );

    void setCompaniesErrorStyle( boolean hasError );

    void setProductsErrorStyle( boolean hasError );

    void setManagersErrorStyle( boolean hasError );

    void setInitiatorsErrorStyle( boolean hasError );

    void setUserFilterControlsVisibility( boolean hasVisible );

    void toggleMsgSearchThreshold();

    void setStateFilter(Selector.SelectorFilter<En_CaseState> filter);

    void setInitiatorCompaniesSupplier(Supplier<Set<EntityOption>> collectionSupplier);

    void updateInitiators();
}