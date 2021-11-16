package ru.protei.portal.ui.common.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeMultiSelector;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.issueimportance.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.issuestate.IssueStatesOptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.casetag.CaseTagMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.AsyncPersonModel;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.plan.selector.PlanButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.platform.PlatformMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.selector.worktrigger.WorkTriggerButtonMultiSelector;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.util.AlternativeKeyboardLayoutTextService.makeAlternativeSearchString;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.searchCaseNumber;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.fromDateRange;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

public class IssueFilterParamView extends Composite implements AbstractIssueFilterParamView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortDir.setValue(false);
        fillDateRanges(dateCreatedRange);
        fillDateRanges(dateModifiedRange);
        dateCreatedRange.setHeader(lang.created());
        dateModifiedRange.setHeader(lang.updated());
        initiators.setPersonModel( initiatorsModel );
        managersModel.setIsFired(null);
        managers.setPersonModel(managersModel);
        managers.setNullItem(() -> new PersonShortView(lang.employeeWithoutManager(), CrmConstants.Employee.UNDEFINED));
        creators.setAsyncSearchModel(creatorsModel);
        searchByCommentsWarning.setText(
                lang.searchByCommentsUnavailable(CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS));
        products.setTypes(En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
    }

    @Override
    public void setModel(AbstractIssueFilterModel model) {
        this.model = model;
    }

    @Override
    public void setInitiatorCompaniesModel(AsyncSelectorModel companyModel) {
        companies.setAsyncModel(companyModel);
        updateInitiators(companies.getValue());
    }

    @Override
    public void setManagerCompaniesModel(AsyncSelectorModel companyModel) {
        managerCompanies.setAsyncModel(companyModel);
        updateManagers(managerCompanies.getValue());
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public HasVisibility searchByCommentsWarningVisibility() {
        return searchByCommentsWarning;
    }

    @Override
    public void setCreatedRangeMandatory(boolean isMandatory) {
        dateCreatedRange.setTypeMandatory(isMandatory);
    }

    @Override
    public void setCreatedRangeValid(boolean isTypeValid, boolean isRangeValid) {
        dateCreatedRange.setValid(isTypeValid, isRangeValid);
    }

    @Override
    public void setModifiedRangeValid(boolean isTypeValid, boolean isRangeValid) {
        dateModifiedRange.setValid(isTypeValid, isRangeValid);
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public HasValue<Set<EntityOption>> managerCompanies() {
        return managerCompanies;
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
    }

    @Override
    public HasValue<Set<CaseTag>> tags() {
        return tags;
    }

    @Override
    public HasValue<Set<CaseState>> states() {
        return state;
    }

    @Override
    public HasVisibility productsVisibility() {
        return products;
    }

    @Override
    public HasVisibility creatorsVisibility() {
        return creators;
    }

    @Override
    public HasVisibility commentAuthorsVisibility() {
        return commentAuthors;
    }

    @Override
    public HasVisibility timeElapsedVisibility() {
        return timeElapsedTypes;
    }

    @Override
    public HasVisibility searchPrivateVisibility() {
        return searchPrivateContainer;
    }

    @Override
    public HasVisibility planVisibility() {
        return plan;
    }

    @Override
    public HasVisibility initiatorsVisibility() {
        return initiators;
    }

    @Override
    public HasVisibility managersVisibility() {
        return managers;
    }

    @Override
    public void resetFilter(DateIntervalWithType dateModified) {
        companies.setValue(null);
        initiators.setValue(null);
        platforms.setValue(null);
        updateInitiators(companies.getValue());
        managerCompanies.setValue(null);
        managers.setValue(null);
        updateManagers(managerCompanies.getValue());
        products.setValue(null);
        commentAuthors.setValue(null);
        timeElapsedTypes.setValue(null);
        creators.setValue(null);
        importance.setValue(null);
        state.setValue(null);
        dateCreatedRange.setValue(null);
        dateModifiedRange.setValue(dateModified);
        sortField.setValue(En_SortField.issue_number);
        sortDir.setValue(false);
        search.setValue("");
        searchByComments.setValue(false);
        toggleMsgSearchThreshold();
        searchPrivate.setValue(null);
        searchFavorite.setValue(null);
        tags.setValue(null);
        tags.isProteiUser( policyService.hasSystemScopeForPrivilege( En_Privilege.ISSUE_VIEW ) );
        plan.setValue(null);
        workTriggers.setValue(null);
        overdueDeadlines.setValue(null);

        if (isAttached()) {
            onFilterChanged();
        }
    }

    @Override
    public void presetCompany(Company company) {
        HashSet<EntityOption> companyIds = new HashSet<>();
        companyIds.add(toEntityOption(company));
        companies.setValue(companyIds);
        updateInitiators(companyIds);
    }

    @Override
    public void presetManagerCompany(Company company) {
        HashSet<EntityOption> managerCompanies = new HashSet<>();
        managerCompanies.add(toEntityOption(company));
        this.managerCompanies.setValue(managerCompanies);
        updateManagers(managerCompanies);
    }

    @Override
    public void presetManagerCompanies(List<EntityOption> companies) {
        HashSet<EntityOption> managerCompanies = new HashSet<>(companies);
        this.managerCompanies.setValue(managerCompanies);
        updateManagers(managerCompanies);
    }

    private void toggleMsgSearchThreshold() {
        if (searchByComments.getValue()) {
            int actualLength = search.getValue().length();
            if (actualLength >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS) {
                searchByCommentsWarning.setVisible(false);
            } else {
                searchByCommentsWarning.setText(lang.searchByCommentsUnavailable(CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS));
                searchByCommentsWarning.setVisible(true);
            }
        } else if (searchByCommentsWarning.isVisible()) {
            searchByCommentsWarning.setVisible(false);
        }
    }

    @Override
    public void fillFilterFields(CaseQuery caseQuery, SelectorsParams filter) {
        search.setValue(caseQuery.getSearchString());
        searchByComments.setValue(caseQuery.isSearchStringAtComments());
        searchPrivate.setValue(caseQuery.isViewPrivate());
        searchFavorite.setValue(caseQuery.getPersonIdToIsFavorite() == null ? null : caseQuery.getPersonIdToIsFavorite().getB());
        sortDir.setValue(caseQuery.getSortDir() == null ? null : caseQuery.getSortDir().equals(En_SortDir.ASC));
        sortField.setValue(caseQuery.getSortField() == null ? En_SortField.creation_date : caseQuery.getSortField());
        dateCreatedRange.setValue(fromDateRange(caseQuery.getCreatedRange()));
        dateModifiedRange.setValue(fromDateRange(caseQuery.getModifiedRange()));
        importance.setValue(toSet(caseQuery.getImportanceIds(), (Function<Integer, ImportanceLevel>) ImportanceLevel::new));
        state.setValue(toSet(caseQuery.getStateIds(), id -> new CaseState(id)));

        Set<EntityOption> initiatorsCompanies = applyCompanies( filter.getCompanyEntityOptions(), caseQuery.getCompanyIds() );
        companies.setValue(initiatorsCompanies);
        Set<EntityOption> managersCompanies = applyCompanies( filter.getCompanyEntityOptions(), caseQuery.getManagerCompanyIds() );
        managerCompanies.setValue(managersCompanies);

        updateManagers(managersCompanies);
        updateInitiators(initiatorsCompanies);

        initiators.setValue(applyPersons(filter.getPersonShortViews(), caseQuery.getInitiatorIds()));
        platforms.setValue(applyPlatforms(filter.getPlatforms(), caseQuery.getPlatformIds()));
        commentAuthors.setValue(applyPersons(filter.getPersonShortViews(), caseQuery.getCommentAuthorIds()));
        timeElapsedTypes.setValue(toSet(caseQuery.getTimeElapsedTypeIds(), id -> En_TimeElapsedType.findById(id)));
        creators.setValue(applyPersons(filter.getPersonShortViews(), caseQuery.getCreatorIds()));
        plan.setValue(filter.getPlanOption());
        workTriggers.setValue(toSet(caseQuery.getWorkTriggersIds(), id -> En_WorkTrigger.findById(id)));
        overdueDeadlines.setValue(caseQuery.getOverdueDeadlines());

        Set<PersonShortView> personShortViews = new LinkedHashSet<>();
        if (emptyIfNull(caseQuery.getManagerIds()).contains(CrmConstants.Employee.UNDEFINED)) {
            personShortViews.add(new PersonShortView(lang.employeeWithoutManager(), CrmConstants.Employee.UNDEFINED));
        }
        personShortViews.addAll(applyPersons(filter.getPersonShortViews(), caseQuery.getManagerIds()));
        managers.setValue(personShortViews);

        Set<ProductShortView> productsShortView = new LinkedHashSet<>();
        if (emptyIfNull(caseQuery.getProductIds()).contains(CrmConstants.Product.UNDEFINED)) {
            productsShortView.add(new ProductShortView(CrmConstants.Product.UNDEFINED, lang.productWithout(), 0));
        }
        productsShortView.addAll(emptyIfNull(filter.getProductShortViews()));
        products.setValue(productsShortView);

        tags.setValue(setOf( filter.getCaseTags() ) );
        toggleMsgSearchThreshold();

        onFilterChanged();
    }

    @Override
    public CaseQuery getFilterFields(En_CaseFilterType filterType) {
        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.CRM_SUPPORT);

        switch (filterType) {
            case CASE_OBJECTS: {
                String searchString = search.getValue();
                query.setCaseNumbers(searchCaseNumber(searchString, searchByComments.getValue()));
                if (isSearchOnlyByCaseNumber(query)) {
                    break;
                }
                query.setSearchStringAtComments(searchByComments.getValue());
                query.setSearchString(isBlank(searchString) ? null : searchString);
                query.setAlternativeSearchString( makeAlternativeSearchString( searchString));
                query.setViewPrivate(searchPrivate.getValue());
                query.setPersonIdToIsFavorite(searchFavorite.getValue() == null ? null : new Pair<>(policyService.getProfileId(), searchFavorite.getValue()));
                query.setSortField(sortField.getValue());
                query.setSortDir(sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setManagerIds(getManagersIdList(managers.getValue()));
                query.setInitiatorIds(getManagersIdList(initiators.getValue()));
                query.setPlatformIds(getPlatformIdList(platforms.getValue()));
                query.setImportanceLevels(nullIfEmpty(importance.getValue()));
                query.setStateIds(nullIfEmpty(toList(states().getValue(), CaseState::getId)));
                query.setCommentAuthorIds(getManagersIdList(commentAuthors.getValue()));
                query.setCaseTagsIds(nullIfEmpty(toList(tags.getValue(), caseTag -> caseTag == null ? CrmConstants.CaseTag.NOT_SPECIFIED : caseTag.getId())));
                query.setCreatorIds(nullIfEmpty(toList(creators.getValue(), personShortView -> personShortView == null ? null : personShortView.getId())));
                query.setManagerCompanyIds(getCompaniesIdList(managerCompanies.getValue()));
                query.setPlanId(plan.getValue() == null ? null : plan.getValue().getId());
                query.setWorkTriggersIds(nullIfEmpty(toList(workTriggers.getValue(),
                        workTrigger -> workTrigger == null ? En_WorkTrigger.NONE.getId() : workTrigger.getId())));
                query.setOverdueDeadlines(overdueDeadlines.getValue());

                query.setCreatedRange(toDateRange(dateCreatedRange.getValue()));
                query.setModifiedRange(toDateRange(dateModifiedRange.getValue()));
                break;
            }
            case CASE_TIME_ELAPSED: {
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setCommentAuthorIds(getManagersIdList(commentAuthors.getValue()));
                query.setTimeElapsedTypeIds(toList(timeElapsedTypes.getValue(), En_TimeElapsedType::getId));
                query.setCreatedRange(toDateRange(dateCreatedRange.getValue()));
                query.setCaseTagsIds(nullIfEmpty(toList(tags.getValue(), caseTag -> caseTag == null ? CrmConstants.CaseTag.NOT_SPECIFIED : caseTag.getId())));
                break;
            }
            case CASE_RESOLUTION_TIME:
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setCaseTagsIds(nullIfEmpty(toList(tags.getValue(), caseTag -> caseTag == null ? CrmConstants.CaseTag.NOT_SPECIFIED : caseTag.getId())));
                query.setImportanceLevels(nullIfEmpty(importance.getValue()));
                query.setStateIds(nullIfEmpty(toList(state.getValue(), CaseState::getId)));
                query.setCreatedRange(toDateRange(dateCreatedRange.getValue()));
                break;
            case PROJECT:
                break;
            case NIGHT_WORK:
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setCommentAuthorIds(getManagersIdList(commentAuthors.getValue()));
                query.setCreatedRange(toDateRange(dateCreatedRange.getValue()));
                break;
        }
        return query;
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<CaseState> caseStateFilter) {
        state.setFilter(caseStateFilter);
    }

    @Override
    public int statesSize() {
        return state.getValues().size();
    }

    @Override
    public int importanceSize() {
        return importance.getValues().size();
    }

    @Override
    public void resetRanges() {
        dateCreatedRange.setValue(null);
        dateModifiedRange.setValue(null);
    }

    private void fillDateRanges (TypedSelectorRangePicker rangePicker) {
        rangePicker.fillSelector(En_DateIntervalType.issueTypes());
    }

    private boolean isSearchOnlyByCaseNumber(CaseQuery query) {
        return query.getCaseNumbers() != null;
    }

    @UiHandler("search")
    public void onSearchChanged(ValueChangeEvent<String> event) {
        startFilterChangedTimer();
    }

    @UiHandler("searchByComments")
    public void onSearchByCommentsChanged(ValueChangeEvent<Boolean> event) {
        onFilterChanged();
    }

    @UiHandler({"dateCreatedRange", "dateModifiedRange"})
    public void onDateRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        onFilterChanged();
    }

    @UiHandler("sortField")
    public void onSortFieldSelected(ValueChangeEvent<En_SortField> event) {
        onFilterChanged();
    }

    @UiHandler("sortDir")
    public void onSortDirClicked(ClickEvent event) {
        onFilterChanged();
    }

    @UiHandler("products")
    public void onProductsSelected(ValueChangeEvent<Set<ProductShortView>> event) {
        onFilterChanged();
    }

    @UiHandler("companies")
    public void onCompaniesSelected(ValueChangeEvent<Set<EntityOption>> event) {
        updateInitiators( companies.getValue() );
        onFilterChanged();
    }

    @UiHandler("managerCompanies")
    public void onManagerCompaniesSelected(ValueChangeEvent<Set<EntityOption>> event) {
        updateManagers( managerCompanies.getValue() );
        onFilterChanged();
    }

    @UiHandler("initiators")
    public void onInitiatorsSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onFilterChanged();
    }

    @UiHandler("platforms")
    public void onPlatformChanged(ValueChangeEvent<Set<PlatformOption>> event) {
        onFilterChanged();
    }

    @UiHandler("managers")
    public void onManagersSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onFilterChanged();
    }

    @UiHandler("commentAuthors")
    public void onCommentAuthorsSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onFilterChanged();
    }

    @UiHandler("tags")
    public void onTagsSelected(ValueChangeEvent<Set<CaseTag>> event) {
        onFilterChanged();
    }

    @UiHandler("searchPrivate")
    public void onSearchOnlyPrivateChanged(ValueChangeEvent<Boolean> event) {
        onFilterChanged();
    }

    @UiHandler("searchFavorite")
    public void onSearchFavoriteChanged(ValueChangeEvent<Boolean> event) {
        onFilterChanged();
    }

    @UiHandler("importance")
    public void onImportanceSelected(ValueChangeEvent<Set<ImportanceLevel>> event) {
        onFilterChanged();
    }

    @UiHandler("state")
    public void onStateSelected(ValueChangeEvent<Set<CaseState>> event) {
        onFilterChanged();
    }

    @UiHandler("creators")
    public void onCreatorSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onFilterChanged();
    }

    @UiHandler("plan")
    public void onPlanChanged(ValueChangeEvent<PlanOption> event) {
        onPlanChanged(event.getValue() != null);
        onFilterChanged();
    }

    @UiHandler("workTriggers")
    public void onWorkTriggersChanged(ValueChangeEvent<Set<En_WorkTrigger>> event) {
        onFilterChanged();
    }

    @UiHandler("overdueDeadlines")
    public void onOverdueDeadlinesChanged(ValueChangeEvent<Boolean> event) {
        onFilterChanged();
    }

    public void applyVisibility(En_CaseFilterType filterType) {
        if (filterType == null) {
            return;
        }

        final boolean isCustomer = isCustomer();
        final boolean isSubcontractor = policyService.isSubcontractorCompany();

        search.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        searchFavoriteContainer.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        searchByComments.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        if (filterType.equals(En_CaseFilterType.CASE_OBJECTS)) {
            dateCreatedRange.setHeader(lang.created());
            dateModifiedRange.removeStyleName(HIDE);
            sortByContainer.removeClassName(HIDE);
        } else {
            dateCreatedRange.setHeader(lang.period());
            dateModifiedRange.addStyleName(HIDE);
            sortByContainer.addClassName(HIDE);
        }
        creators.setVisible(!isCustomer && filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        initiators.setVisible((!isCustomer || !isSubcontractor) && filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        platforms.setVisible(!isCustomer && filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        managerCompanies.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        managers.setVisible((!isCustomer || isSubcontractor) && filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        commentAuthors.setVisible(filterType.equals(En_CaseFilterType.CASE_TIME_ELAPSED) || filterType.equals(En_CaseFilterType.NIGHT_WORK));
        timeElapsedTypes.setVisible(filterType.equals(En_CaseFilterType.CASE_TIME_ELAPSED));
        tags.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS) || filterType.equals(En_CaseFilterType.CASE_RESOLUTION_TIME) ||
                        filterType.equals(En_CaseFilterType.CASE_TIME_ELAPSED));
        searchPrivateContainer.setVisible(!isCustomer && filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        plan.setVisible(!isCustomer && filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        workTriggers.setVisible(!isCustomer && filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        overdueDeadlinesContainer.setVisible(!isCustomer && filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        if (filterType.equals(En_CaseFilterType.CASE_TIME_ELAPSED) || filterType.equals(En_CaseFilterType.NIGHT_WORK)) {
            importanceContainer.addClassName(HIDE);
            stateContainer.addClassName(HIDE);
        } else {
            importanceContainer.removeClassName(HIDE);
            stateContainer.removeClassName(HIDE);
        }
    }

    private boolean isCustomer() {
        return !policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
    }

    public String validateMultiSelectorsTotalCount() {
        if (managerCompanies.getValue().size() > 100) {
            setManagerCompaniesErrorStyle(true);
            return lang.errTooMuchCompanies();
        } else {
            setManagerCompaniesErrorStyle(false);
        }
        if (companies.getValue().size() > 100){
            setCompaniesErrorStyle(true);
            return lang.errTooMuchCompanies();
        } else {
            setCompaniesErrorStyle(false);
        }
        if (products.getValue().size() > 50){
            setProductsErrorStyle(true);
            return lang.errTooMuchProducts();
        } else {
            setProductsErrorStyle(false);
        }
        if (managers.getValue().size() > 50){
            setManagersErrorStyle(true);
            return lang.errTooMuchManagers();
        } else {
            setManagersErrorStyle(false);
        }
        if (initiators.getValue().size() > 50){
            setInitiatorsErrorStyle(true);
            return lang.errTooMuchInitiators();
        } else {
            setInitiatorsErrorStyle(false);
        }
        if (platforms.getValue().size() > 50){
            setPlatformsErrorStyle(true);
            return lang.errTooMuchPlatforms();
        } else {
            setPlatformsErrorStyle(false);
        }
        return null;
    }

    public boolean isSearchFieldCorrect(){
        return !searchByComments.getValue() ||
                search.getValue().length() >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS;
    }

    public boolean isCreatedRangeTypeValid() {
        return !dateCreatedRange.isTypeMandatory()
               || (dateCreatedRange.getValue() != null
                   && dateCreatedRange.getValue().getIntervalType() != null);
    }

    public boolean isCreatedRangeValid() {
        return isDateRangeValid(dateCreatedRange.getValue());
    }

    public boolean isModifiedRangeValid() {
        return isDateRangeValid(dateModifiedRange.getValue());
    }

    public boolean isDateRangeValid(DateIntervalWithType dateRange) {
        if (dateRange == null || dateRange.getIntervalType() == null) {
            return true;
        }

        return !Objects.equals(dateRange.getIntervalType(), En_DateIntervalType.FIXED) || dateRange.getInterval().isValid();
    }

    private Set<PersonShortView> applyPersons(List<PersonShortView> personShortViews, List<Long> personIds) {
        return stream(personShortViews)
                .filter(personShortView ->
                        stream(personIds).anyMatch(ids -> ids.equals(personShortView.getId())))
                .collect(Collectors.toSet());
    }

    private Set<PlatformOption> applyPlatforms(List<PlatformOption> platforms, List<Long> platformIds) {
        return stream(platforms)
                .filter(platform ->
                        stream(platformIds).anyMatch(ids -> ids.equals(platform.getId())))
                .collect(Collectors.toSet());
    }

    private Set<EntityOption> applyCompanies(List<EntityOption> companies, List<Long> companyIds) {
        return stream(companies)
                .filter(company ->
                        stream(companyIds).anyMatch(ids -> ids.equals(company.getId())))
                .collect(Collectors.toSet());
    }

    private void ensureDebugIds() {
        search.setDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        search.setDebugIdAction(DebugIds.FILTER.SEARCH_CLEAR_BUTTON);
        plan.setEnsureDebugId(DebugIds.FILTER.PLAN_SELECTOR);
        searchByComments.ensureDebugId(DebugIds.FILTER.SEARCH_BY_COMMENTS_TOGGLE);
        searchByCommentsWarning.ensureDebugId(DebugIds.FILTER.SEARCH_BY_WARNING_COMMENTS_LABEL);
        dateCreatedRange.setEnsureDebugId(DebugIds.FILTER.DATE_CREATED_RANGE_CONTAINER);
        dateModifiedRange.setEnsureDebugId(DebugIds.FILTER.DATE_MODIFIED_RANGE_CONTAINER);
        sortField.setEnsureDebugId(DebugIds.FILTER.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.FILTER.SORT_DIR_BUTTON);
        companies.setAddEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_ADD_BUTTON);
        companies.setClearEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_CLEAR_BUTTON);
        companies.setItemContainerEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_ITEM_CONTAINER);
        companies.setLabelEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_LABEL);
        managerCompanies.setAddEnsureDebugId(DebugIds.FILTER.MANAGER_COMPANY_SELECTOR_ADD_BUTTON);
        managerCompanies.setClearEnsureDebugId(DebugIds.FILTER.MANAGER_COMPANY_SELECTOR_CLEAR_BUTTON);
        managerCompanies.setItemContainerEnsureDebugId(DebugIds.FILTER.MANAGER_COMPANY_SELECTOR_ITEM_CONTAINER);
        managerCompanies.setLabelEnsureDebugId(DebugIds.FILTER.MANAGER_COMPANY_SELECTOR_LABEL);
        products.setAddEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_ADD_BUTTON);
        products.setClearEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_CLEAR_BUTTON);
        products.setItemContainerEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_ITEM_CONTAINER);
        products.setLabelEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_LABEL);
        managers.setAddEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_ADD_BUTTON);
        managers.setClearEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_CLEAR_BUTTON);
        managers.setItemContainerEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_ITEM_CONTAINER);
        managers.setLabelEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_LABEL);
        initiators.setAddEnsureDebugId(DebugIds.FILTER.INITIATORS_SELECTOR_ADD_BUTTON);
        initiators.setClearEnsureDebugId(DebugIds.FILTER.INITIATORS_SELECTOR_CLEAR_BUTTON);
        initiators.setItemContainerEnsureDebugId(DebugIds.FILTER.INITIATORS_SELECTOR_ITEM_CONTAINER);
        initiators.setLabelEnsureDebugId(DebugIds.FILTER.INITIATORS_SELECTOR_LABEL);
        platforms.setAddEnsureDebugId(DebugIds.FILTER.PLATFORMS_SELECTOR_ADD_BUTTON);
        platforms.setClearEnsureDebugId(DebugIds.FILTER.PLATFORMS_SELECTOR_CLEAR_BUTTON);
        platforms.setItemContainerEnsureDebugId(DebugIds.FILTER.PLATFORMS_SELECTOR_ITEM_CONTAINER);
        platforms.setLabelEnsureDebugId(DebugIds.FILTER.PLATFORMS_SELECTOR_LABEL);
        searchPrivate.setYesEnsureDebugId(DebugIds.FILTER.PRIVACY_YES_BUTTON);
        searchPrivate.setNotDefinedEnsureDebugId(DebugIds.FILTER.PRIVACY_NOT_DEFINED_BUTTON);
        searchPrivate.setNoEnsureDebugId(DebugIds.FILTER.PRIVACY_NO_BUTTON);
        searchFavorite.setYesEnsureDebugId(DebugIds.FILTER.FAVORITE_YES_BUTTON);
        searchFavorite.setNotDefinedEnsureDebugId(DebugIds.FILTER.FAVORITE_NOT_DEFINED_BUTTON);
        searchFavorite.setNoEnsureDebugId(DebugIds.FILTER.FAVORITE_NO_BUTTON);
        tags.setAddEnsureDebugId(DebugIds.FILTER.TAG_SELECTOR_ADD_BUTTON);
        tags.setClearEnsureDebugId(DebugIds.FILTER.TAG_SELECTOR_CLEAR_BUTTON);
        tags.setItemContainerEnsureDebugId(DebugIds.FILTER.TAG_SELECTOR_ITEM_CONTAINER);
        tags.setLabelEnsureDebugId(DebugIds.FILTER.TAG_SELECTOR_LABEL);
        labelSortBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.SORT_FIELD_LABEL);
        labelSearchPrivate.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.PRIVACY_LABEL);
        labelIssueImportance.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.ISSUE_IMPORTANCE_LABEL);
        labelIssueState.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.ISSUE_STATE_LABEL);
        creators.ensureDebugId(DebugIds.FILTER.CREATOR_SELECTOR);
        creators.setAddEnsureDebugId(DebugIds.FILTER.CREATOR_ADD_BUTTON);
        creators.setClearEnsureDebugId(DebugIds.FILTER.CREATOR_CLEAR_BUTTON);
        creators.setItemContainerEnsureDebugId(DebugIds.FILTER.CREATOR_ITEM_CONTAINER);
        timeElapsedTypes.ensureDebugId(DebugIds.ISSUE_REPORT.TIME_ELAPSED_TYPES);
        timeElapsedTypes.setAddEnsureDebugId(DebugIds.ISSUE_REPORT.TIME_ELAPSED_TYPES_ADD_BUTTON);
        timeElapsedTypes.setClearEnsureDebugId(DebugIds.ISSUE_REPORT.TIME_ELAPSED_TYPES_CLEAR_BUTTON);
        timeElapsedTypes.setItemContainerEnsureDebugId(DebugIds.ISSUE_REPORT.TIME_ELAPSED_TYPES_ITEM_CONTAINER);
        timeElapsedTypes.setLabelEnsureDebugId(DebugIds.ISSUE_REPORT.TIME_ELAPSED_TYPES_LABEL);
        workTriggers.ensureDebugId(DebugIds.ISSUE_REPORT.WORK_TRIGGER_TYPES);
        workTriggers.setAddEnsureDebugId(DebugIds.ISSUE_REPORT.WORK_TRIGGER_TYPES_ADD_BUTTON);
        workTriggers.setClearEnsureDebugId(DebugIds.ISSUE_REPORT.WORK_TRIGGER_TYPES_CLEAR_BUTTON);
        workTriggers.setItemContainerEnsureDebugId(DebugIds.ISSUE_REPORT.WORK_TRIGGER_TYPES_ITEM_CONTAINER);
        workTriggers.setLabelEnsureDebugId(DebugIds.ISSUE_REPORT.WORK_TRIGGER_TYPES_LABEL);
        overdueDeadlines.setYesEnsureDebugId(DebugIds.FILTER.OVERDUE_DEADLINES_YES_BUTTON);
        overdueDeadlines.setNotDefinedEnsureDebugId(DebugIds.FILTER.OVERDUE_DEADLINES_NOT_DEFINED_BUTTON);
        overdueDeadlines.setNoEnsureDebugId(DebugIds.FILTER.OVERDUE_DEADLINES_NO_BUTTON);
    }

    private void onFilterChanged() {
        if (model != null) {
            model.onUserFilterChanged();
        }
    }

    private void onPlanChanged(boolean isPresent) {
        if (model != null) {
            model.onPlanPresent(isPresent);
        }
    }

    private void startFilterChangedTimer() {
        if (timer == null) {
            timer = new Timer() {
                @Override
                public void run() {
                    toggleMsgSearchThreshold();
                    onFilterChanged();
                }
            };
        } else {
            timer.cancel();
        }
        timer.schedule(300);
    }

    private void setCompaniesErrorStyle(boolean hasError) {
        if (hasError) {
            companies.addStyleName(REQUIRED);
        } else {
            companies.removeStyleName(REQUIRED);
        }
    }

    private void setManagerCompaniesErrorStyle(boolean hasError) {
        if (hasError) {
            managerCompanies.addStyleName(REQUIRED);
        } else {
            managerCompanies.removeStyleName(REQUIRED);
        }
    }

    private void setProductsErrorStyle(boolean hasError) {
        if (hasError) {
            products.addStyleName(REQUIRED);
        } else {
            products.removeStyleName(REQUIRED);
        }
    }

    private void setManagersErrorStyle(boolean hasError) {
        if (hasError) {
            managers.addStyleName(REQUIRED);
        } else {
            managers.removeStyleName(REQUIRED);
        }
    }

    private void setInitiatorsErrorStyle(boolean hasError) {
        if (hasError) {
            initiators.addStyleName(REQUIRED);
        } else {
            initiators.removeStyleName(REQUIRED);
        }
    }

    private void setPlatformsErrorStyle(boolean hasError) {
        if (hasError) {
            platforms.addStyleName(REQUIRED);
        } else {
            platforms.removeStyleName(REQUIRED);
        }
    }

    private void updateInitiators( Set<EntityOption> initiatorsCompanies ) {
        Set<Long> companyIds = toSet( initiatorsCompanies, entityOption -> entityOption.getId() );
        initiatorsModel.updateCompanies(initiators, companyIds );
        if (isEmpty( companyIds )) {
            initiators.setValue( null );
        }
    }

    private void updateManagers(Set<EntityOption> managersCompanies) {
        Set<Long> companyIds = toSet( managersCompanies, entityOption -> entityOption.getId() );
        managersModel.updateCompanies( managers, companyIds );
        if (isEmpty( companyIds )) {
            managers.setValue( null );
        }
    }

    private static Set< Long > getProductsIdList(Set<ProductShortView> productSet) {

        if ( isEmpty(productSet) ) {
            return null;
        }
        return productSet
                .stream()
                .map(productShortView -> productShortView.getId())
                .collect( Collectors.toSet() );
    }

    private static List< Long > getCompaniesIdList(Set<EntityOption> companySet) {

        if ( isEmpty(companySet) ) {
            return null;
        }
        return collectIds(companySet);
    }

    private static EntityOption toEntityOption(Company company) {
        if ( company == null  ) {
            return null;
        }
        EntityOption option = new EntityOption();
        option.setId( company.getId() );
        option.setDisplayText( company.getCname() );
        return option;
    }

    private static List< Long > getManagersIdList(Set<PersonShortView> personSet) {

        if ( isEmpty(personSet) ) {
            return null;
        }
        return collectIds(personSet);
    }

    private List<Long> getPlatformIdList(Collection<PlatformOption> platforms) {

        if (isEmpty(platforms)){
            return null;
        }
        return collectIds(platforms);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    CleanableSearchBox search;
    @UiField
    HTMLPanel searchByCommentsContainer;
    @UiField
    Label searchByCommentsWarning;
    @UiField
    CheckBox searchByComments;
    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker dateCreatedRange;
    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker dateModifiedRange;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector products;
    @Inject
    @UiField(provided = true)
    CompanyMultiSelector companies;
    @Inject
    @UiField(provided = true)
    PersonMultiSelector initiators;
    @Inject
    @UiField(provided = true)
    PlatformMultiSelector platforms;
    @Inject
    @UiField(provided = true)
    CompanyMultiSelector managerCompanies;
    @Inject
    @UiField(provided = true)
    PersonMultiSelector managers;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector commentAuthors;
    @Inject
    @UiField(provided = true)
    ElapsedTimeTypeMultiSelector timeElapsedTypes;
    @Inject
    @UiField(provided = true)
    PersonMultiSelector creators;
    @Inject
    @UiField(provided = true)
    CaseTagMultiSelector tags;
    @Inject
    @UiField(provided = true)
    PlanButtonSelector plan;
    @UiField
    HTMLPanel searchPrivateContainer;
    @UiField
    HTMLPanel searchFavoriteContainer;
    @UiField
    ThreeStateButton searchPrivate;
    @UiField
    ThreeStateButton searchFavorite;
    @UiField
    LabelElement labelSortBy;
    @UiField
    LabelElement labelSearchPrivate;
    @UiField
    LabelElement labelIssueImportance;
    @UiField
    LabelElement labelIssueState;
    @Inject
    @UiField(provided = true)
    ImportanceBtnGroupMulti importance;
    @Inject
    @UiField(provided = true)
    IssueStatesOptionList state;
    @Inject
    @UiField(provided = true)
    WorkTriggerButtonMultiSelector workTriggers;
    @UiField
    HTMLPanel overdueDeadlinesContainer;
    @UiField
    ThreeStateButton overdueDeadlines;

    @UiField
    DivElement sortByContainer;

    @UiField
    DivElement importanceContainer;
    @UiField
    DivElement stateContainer;

    @Inject
    PolicyService policyService;

    @Inject
    PersonModel initiatorsModel;
    @Inject
    PersonModel managersModel;
    @Inject
    AsyncPersonModel creatorsModel;


    private Timer timer = null;
    private AbstractIssueFilterModel model;


    interface IssueFilterUiBinder extends UiBinder<HTMLPanel, IssueFilterParamView> {}
    private static IssueFilterUiBinder ourUiBinder = GWT.create(IssueFilterUiBinder.class);
}
