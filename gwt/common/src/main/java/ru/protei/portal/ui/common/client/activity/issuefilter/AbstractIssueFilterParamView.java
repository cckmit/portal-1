package ru.protei.portal.ui.common.client.activity.issuefilter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface AbstractIssueFilterParamView extends IsWidget {
    void setModel(AbstractIssueFilterModel model);

    void setInitiatorModel(InitiatorModel initiatorModel);

    void setCreatorModel(PersonModel personModel);

    HasValue<String> searchPattern();

    HasValue<Boolean> searchByComments();

    HasValue<DateInterval> dateCreatedRange();

    HasValue<DateInterval> dateModifiedRange();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<Set<ProductShortView>> products();

    HasValue<Set<EntityOption>> companies();

    HasValue<Set<PersonShortView>> initiators();

    HasValue<Set<PersonShortView>> managers();

    HasValue<Set<PersonShortView>> commentAuthors();

    HasValue<Set<PersonShortView>> creators();

    HasValue<Set<CaseTag>> tags();

    HasValue<Boolean> searchPrivate();

    HasValue<Set<En_ImportanceLevel>> importances();

    HasValue<Set<En_CaseState>> states();

    HasVisibility searchByCommentsWarningVisibility();

    HasVisibility productsVisibility();

    HasVisibility managersVisibility();

    HasVisibility commentAuthorsVisibility();

    HasVisibility searchPrivateVisibility();

    void resetFilter();

    void presetCompany(Company company);

    void fillFilterFields(CaseQuery caseQuery, SelectorsParams selectorsParams);

    CaseQuery getFilterFields(En_CaseFilterType filterType);

    void setStateFilter(Selector.SelectorFilter<En_CaseState> caseStateFilter);

    void fillImportanceButtons(List<En_ImportanceLevel> importanceLevelList);

    void setInitiatorCompaniesSupplier(Supplier<Set<EntityOption>> collectionSupplier);

    String validateMultiSelectorsTotalCount();

    boolean isSearchFieldCorrect();

    void watchForScrollOf(Widget widget);

    void stopWatchForScrollOf(Widget widget);

    void applyVisibilityByFilterType(En_CaseFilterType filterType);
}
