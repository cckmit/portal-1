package ru.protei.portal.ui.common.client.activity.issuefilter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.person.AsyncPersonModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;

import java.util.List;
import java.util.Set;

public interface AbstractIssueFilterParamView extends IsWidget {
    void setModel(AbstractIssueFilterModel model);

    void setInitiatorsModel(PersonModel personModel);

    void setManagersModel(PersonModel personModel);

    void setCreatorModel(AsyncPersonModel asyncPersonModel);

    HasValue<String> searchPattern();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<Set<ProductShortView>> products();

    HasValue<Set<EntityOption>> companies();

    HasValue<Set<EntityOption>> managerCompanies();

    HasValue<Set<PersonShortView>> managers();

    HasValue<Set<CaseTag>> tags();

    HasValue<Set<CaseState>> states();

    HasValue<Set<En_WorkTrigger>> workTriggers();

    HasValue<Boolean> overdueDeadlines();

    HasVisibility searchByCommentsWarningVisibility();

    HasVisibility productsVisibility();

    HasVisibility creatorsVisibility();

    HasVisibility commentAuthorsVisibility();

    HasVisibility timeElapsedVisibility();

    HasVisibility searchPrivateVisibility();

    HasVisibility planVisibility();

    HasVisibility workTriggerVisibility();

    HasVisibility overdueDeadlinesVisibility();

    void resetFilter();

    void presetCompany(Company company);

    void presetManagerCompany(Company company);

    void fillFilterFields(CaseQuery caseQuery, SelectorsParams selectorsParams);

    CaseQuery getFilterFields(En_CaseFilterType filterType);

    void setStateFilter(Selector.SelectorFilter<CaseState> caseStateFilter);

    void fillImportanceButtons(List<En_ImportanceLevel> importanceLevelList);

    String validateMultiSelectorsTotalCount();

    boolean isSearchFieldCorrect();

    boolean isCreatedRangeValid();

    boolean isModifiedRangeValid();

    void applyVisibilityByFilterType(En_CaseFilterType filterType);

    void setCreatedRangeMandatory(boolean isMandatory);

    boolean isCreatedRangeTypeValid();

    void setCreatedRangeValid(boolean isTypeValid, boolean isRangeValid);

    void setModifiedRangeValid(boolean isTypeValid, boolean isRangeValid);
}
