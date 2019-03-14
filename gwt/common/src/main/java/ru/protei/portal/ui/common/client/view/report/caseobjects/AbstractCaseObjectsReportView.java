package ru.protei.portal.ui.common.client.view.report.caseobjects;

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
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;

import java.util.Set;
import java.util.function.Supplier;

public interface AbstractCaseObjectsReportView  extends IsWidget {
    void setActivity( AbstractIssueFilterParamActivity activity);

    AbstractIssueFilterParamActivity getActivity();

    HasValue<String> searchPattern();

    HasValue<Boolean> searchByComments();

    HasValue<DateInterval> dateRange();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<Set<ProductShortView>> products();

    HasValue<Set<EntityOption>> companies();

    HasValue<Set<PersonShortView>> initiators();

    HasValue<Set<PersonShortView>> managers();

    HasValue<Set<PersonShortView>> commentAuthors();

    HasValue<Set<EntityOption>> tags();

    HasValue<Boolean> searchPrivate();

    HasValue<Set<En_ImportanceLevel>> importances();

    HasValue<Set<En_CaseState>> states();

    HasVisibility productsVisibility();

    HasVisibility companiesVisibility();

    HasVisibility managersVisibility();

    HasVisibility commentAuthorsVisibility();

    HasVisibility tagsVisibility();

    HasVisibility searchPrivateVisibility();

    void resetFilter();

    void fillFilterFields( CaseQuery caseQuery);

    void toggleMsgSearchThreshold();

    void setInitiatorCompaniesSupplier( Supplier<Set<EntityOption>> collectionSupplier);

    void updateInitiators();

    HasValue<CaseFilterShortView> userFilter();

}
