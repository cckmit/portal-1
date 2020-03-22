package ru.protei.portal.ui.common.client.widget.issuefilter;

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
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.issueimportance.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.issuestate.IssueStatesOptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.casetag.CaseTagMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.*;

public class IssueFilterParamView extends Composite implements AbstractIssueFilterWidgetView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortDir.setValue(false);
        dateCreatedRange.setPlaceholder(lang.selectDate());
        dateModifiedRange.setPlaceholder(lang.selectDate());
        initiators.setCompaniesSupplier(() -> new HashSet<>( companies.getValue()) );
        searchByCommentsWarning.setText(
                lang.searchByCommentsUnavailable(CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS));
    }

    @Override
    public void setModel(AbstractIssueFilterModel model) {
        this.model = model;
    }

    @Override
    public void setInitiatorModel(InitiatorModel initiatorModel) {
        initiators.setInitiatorModel(initiatorModel);
    }

    @Override
    public void setCreatorModel(PersonModel personModel) {
        creators.setPersonModel(personModel);
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public HasValue<Boolean> searchByComments() {
        return searchByComments;
    }

    @Override
    public HasVisibility searchByCommentsWarningVisibility() {
        return searchByCommentsWarning;
    }

    @Override
    public HasValue<DateInterval> dateCreatedRange() {
        return dateCreatedRange;
    }

    @Override
    public HasValue<DateInterval> dateModifiedRange() {
        return dateModifiedRange;
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
    public HasValue<Set<PersonShortView>> initiators() {
        return initiators;
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
    }

    @Override
    public HasValue<Set<PersonShortView>> commentAuthors() {
        return commentAuthors;
    }

    @Override
    public HasValue<Set<PersonShortView>> creators() {
        return creators;
    }

    @Override
    public HasValue<Set<CaseTag>> tags() {
        return tags;
    }

    @Override
    public HasValue<Boolean> searchPrivate() {
        return searchPrivate;
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> importances() {
        return importance;
    }

    @Override
    public HasValue<Set<En_CaseState>> states() {
        return state;
    }

    @Override
    public HasVisibility productsVisibility() {
        return products;
    }

    @Override
    public HasVisibility managersVisibility() {
        return managers;
    }

    @Override
    public HasVisibility commentAuthorsVisibility() {
        return commentAuthors;
    }

    @Override
    public HasVisibility searchPrivateVisibility() {
        return searchPrivateContainer;
    }

    @Override
    public void resetFilter() {
        companies.setValue(null);
        products.setValue(null);
        managers.setValue(null);
        initiators.setValue(null);
        commentAuthors.setValue(null);
        creators.setValue(null);
        importance.setValue(null);
        state.setValue(null);
        dateCreatedRange.setValue(null);
        dateModifiedRange.setValue(null);
        sortField.setValue(En_SortField.issue_number);
        sortDir.setValue(false);
        search.setValue("");
        searchByComments.setValue(false);
        toggleMsgSearchThreshold();
        searchPrivate.setValue(null);
        tags.setValue(null);
        tags.isProteiUser( policyService.hasSystemScopeForPrivilege( En_Privilege.ISSUE_VIEW ) );

        model.onUserFilterChanged();
    }

    @Override
    public void presetCompany(Company company) {
        HashSet<EntityOption> companyIds = new HashSet<>();
        companyIds.add(IssueFilterUtils.toEntityOption(company));
        companies.setValue(companyIds);
        updateInitiators();
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
        sortDir.setValue(caseQuery.getSortDir() == null ? null : caseQuery.getSortDir().equals(En_SortDir.ASC));
        sortField.setValue(caseQuery.getSortField() == null ? En_SortField.creation_date : caseQuery.getSortField());
        dateCreatedRange.setValue(new DateInterval(caseQuery.getCreatedFrom(), caseQuery.getCreatedTo()));
        dateModifiedRange.setValue(new DateInterval(caseQuery.getModifiedFrom(), caseQuery.getModifiedTo()));
        importance.setValue(getImportances(caseQuery.getImportanceIds()));
        state.setValue(getStates(caseQuery.getStateIds()));

        companies.setValue(new HashSet<>(emptyIfNull(filter.getCompanyEntityOptions())));
        updateInitiators();

        initiators.setValue(applyPersons(filter, caseQuery.getInitiatorIds()));
        commentAuthors.setValue(applyPersons(filter, caseQuery.getCommentAuthorIds()));
        creators.setValue(applyPersons(filter, caseQuery.getCreatorIds()));

        Set<PersonShortView> personShortViews = new LinkedHashSet<>();
        if (emptyIfNull(caseQuery.getManagerIds()).contains(CrmConstants.Employee.UNDEFINED)) {
            personShortViews.add(new PersonShortView(lang.employeeWithoutManager(), CrmConstants.Employee.UNDEFINED));
        }
        personShortViews.addAll(applyPersons(filter, caseQuery.getManagerIds()));
        managers.setValue(personShortViews);

        Set<ProductShortView> productsShortView = new LinkedHashSet<>();
        if (emptyIfNull(caseQuery.getProductIds()).contains(CrmConstants.Product.UNDEFINED)) {
            productsShortView.add(new ProductShortView(CrmConstants.Product.UNDEFINED, lang.productWithout(), 0));
        }
        productsShortView.addAll(emptyIfNull(filter.getProductShortViews()));
        products.setValue(productsShortView);

        tags.setValue(setOf( filter.getCaseTags() ) );
        toggleMsgSearchThreshold();

        model.onUserFilterChanged();
    }

    @Override
    public CaseQuery getFilterFields(En_CaseFilterType filterType) {
        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.CRM_SUPPORT);

        switch (filterType) {
            case CASE_OBJECTS: {
                String searchString = search.getValue();
                query.setCaseNumbers(searchCaseNumber(searchString, searchByComments.getValue()));
                if (query.getCaseNumbers() == null) {
                    query.setSearchStringAtComments(searchByComments.getValue());
                    query.setSearchString(isBlank(searchString) ? null : searchString);
                }
                query.setViewPrivate(searchPrivate.getValue());
                query.setSortField(sortField.getValue());
                query.setSortDir(sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setManagerIds(getManagersIdList(managers.getValue()));
                query.setInitiatorIds(getManagersIdList(initiators.getValue()));
                query.setImportanceIds(getImportancesIdList(importance.getValue()));
                query.setStates(getStateList(state.getValue()));
                query.setCommentAuthorIds(getManagersIdList(commentAuthors.getValue()));
                query.setCaseTagsIds( toList( tags().getValue(), caseTag -> caseTag == null ? CrmConstants.CaseTag.NOT_SPECIFIED : caseTag.getId() ) );
                query.setCreatorIds(toList(creators().getValue(), personShortView -> personShortView == null ? null : personShortView.getId()));

                DateInterval createdInterval = dateCreatedRange().getValue();
                if (createdInterval != null) {
                    query.setCreatedFrom(createdInterval.from);
                    query.setCreatedTo(createdInterval.to);
                }
                DateInterval modifiedInterval = dateModifiedRange().getValue();
                if (modifiedInterval != null) {
                    query.setModifiedFrom(modifiedInterval.from);
                    query.setModifiedTo(modifiedInterval.to);
                }
                break;
            }
            case CASE_TIME_ELAPSED: {
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setCommentAuthorIds(getManagersIdList(commentAuthors.getValue()));
                query = fillCreatedInterval(query, dateCreatedRange.getValue());
                break;
            }
            case CASE_RESOLUTION_TIME:
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setManagerIds(getManagersIdList(managers.getValue()));
                query.setCaseTagsIds( toList( tags.getValue(), caseTag -> caseTag == null ? CrmConstants.CaseTag.NOT_SPECIFIED : caseTag.getId() ) );
                query.setImportanceIds(getImportancesIdList(importance.getValue()));
                query.setStates(getStateList(state.getValue()));
                query = fillCreatedInterval(query, dateCreatedRange.getValue());
                break;
        }
        return query;
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<En_CaseState> caseStateFilter) {
        state.setFilter(caseStateFilter);
    }

    @Override
    public void setInitiatorCompaniesSupplier(Supplier<Set<EntityOption>> collectionSupplier) {
        initiators.setCompaniesSupplier(collectionSupplier);
    }

    @UiHandler("search")
    public void onSearchChanged(ValueChangeEvent<String> event) {
        startFilterChangedTimer();
    }

    @UiHandler("searchByComments")
    public void onSearchByCommentsChanged(ValueChangeEvent<Boolean> event) {
        onFilterChanged();
    }

    @UiHandler("dateCreatedRange")
    public void onDateCreatedRangeChanged(ValueChangeEvent<DateInterval> event) {
        onFilterChanged();
    }

    @UiHandler("dateModifiedRange")
    public void onDateModifiedRangeChanged(ValueChangeEvent<DateInterval> event) {
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
        initiators.updateCompanies();
        onFilterChanged();
    }

    @UiHandler("initiators")
    public void onInitiatorsSelected(ValueChangeEvent<Set<PersonShortView>> event) {
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

    @UiHandler("importance")
    public void onImportanceSelected(ValueChangeEvent<Set<En_ImportanceLevel>> event) {
        onFilterChanged();
    }

    @UiHandler("state")
    public void onStateSelected(ValueChangeEvent<Set<En_CaseState>> event) {
        onFilterChanged();
    }

    @UiHandler("creators")
    public void onCreatorSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onFilterChanged();
    }

    public void watchForScrollOf(Widget widget) {
        sortField.watchForScrollOf(widget);
    }

    public void stopWatchForScrollOf(Widget widget) {
        sortField.stopWatchForScrollOf(widget);
    }

    private Set<PersonShortView> applyPersons(SelectorsParams filter, List<Long> personIds) {
        return emptyIfNull(filter.getPersonShortViews()).stream()
                .filter(personShortView ->
                        emptyIfNull(personIds).stream().anyMatch(ids -> ids.equals(personShortView.getId())))
                .collect(Collectors.toSet());
    }

    private void ensureDebugIds() {
        search.setEnsureDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        search.setEnsureDebugIdAction(DebugIds.FILTER.SEARCH_CLEAR_BUTTON);
        searchByComments.ensureDebugId(DebugIds.FILTER.SEARCH_BY_COMMENTS_TOGGLE);
        searchByCommentsWarning.ensureDebugId(DebugIds.FILTER.SEARCH_BY_WARNING_COMMENTS_LABEL);
        dateCreatedRange.setEnsureDebugId(DebugIds.FILTER.DATE_CREATED_RANGE_INPUT);
        dateCreatedRange.getRelative().ensureDebugId(DebugIds.FILTER.DATE_CREATED_RANGE_BUTTON);
        dateModifiedRange.setEnsureDebugId(DebugIds.FILTER.DATE_MODIFIED_RANGE_INPUT);
        dateModifiedRange.getRelative().ensureDebugId(DebugIds.FILTER.DATE_MODIFIED_RANGE_BUTTON);
        sortField.setEnsureDebugId(DebugIds.FILTER.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.FILTER.SORT_DIR_BUTTON);
        companies.setAddEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_ADD_BUTTON);
        companies.setClearEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_CLEAR_BUTTON);
        companies.setItemContainerEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_ITEM_CONTAINER);
        companies.setLabelEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_LABEL);
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
        searchPrivate.setYesEnsureDebugId(DebugIds.FILTER.PRIVACY_YES_BUTTON);
        searchPrivate.setNotDefinedEnsureDebugId(DebugIds.FILTER.PRIVACY_NOT_DEFINED_BUTTON);
        searchPrivate.setNoEnsureDebugId(DebugIds.FILTER.PRIVACY_NO_BUTTON);
        tags.setAddEnsureDebugId(DebugIds.FILTER.TAG_SELECTOR_ADD_BUTTON);
        tags.setClearEnsureDebugId(DebugIds.FILTER.TAG_SELECTOR_CLEAR_BUTTON);
        tags.setItemContainerEnsureDebugId(DebugIds.FILTER.TAG_SELECTOR_ITEM_CONTAINER);
        tags.setLabelEnsureDebugId(DebugIds.FILTER.TAG_SELECTOR_LABEL);
        labelCreated.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.DATE_CREATED_RANGE_LABEL);
        labelUpdated.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.DATE_MODIFIED_RANGE_LABEL);
        labelSortBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.SORT_FIELD_LABEL);
        labelSearchPrivate.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.PRIVACY_LABEL);
        labelIssueImportance.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.ISSUE_IMPORTANCE_LABEL);
        labelIssueState.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.ISSUE_STATE_LABEL);
        creators.ensureDebugId(DebugIds.FILTER.CREATOR_SELECTOR);
        creators.setAddEnsureDebugId(DebugIds.FILTER.CREATOR_ADD_BUTTON);
        creators.setClearEnsureDebugId(DebugIds.FILTER.CREATOR_CLEAR_BUTTON);
        creators.setItemContainerEnsureDebugId(DebugIds.FILTER.CREATOR_ITEM_CONTAINER);
    }

    private void onFilterChanged() {
        if (model != null) {
            model.onUserFilterChanged();
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

    public void applyVisibilityByFilterType(En_CaseFilterType filterType) {
        if (filterType == null) {
            return;
        }

        search.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        searchByComments.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        if (filterType.equals(En_CaseFilterType.CASE_OBJECTS)) {
            modifiedRangeContainer.removeClassName(HIDE);
            sortByContainer.removeClassName(HIDE);
            labelCreated.setInnerText(lang.created());
        } else {
            modifiedRangeContainer.addClassName(HIDE);
            sortByContainer.addClassName(HIDE);
            labelCreated.setInnerText(lang.period());
        }
        creators.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        initiators.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        managers.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        commentAuthors.setVisible(filterType.equals(En_CaseFilterType.CASE_TIME_ELAPSED));
        tags.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS) || filterType.equals(En_CaseFilterType.CASE_RESOLUTION_TIME));
        searchPrivateContainer.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        if (filterType.equals(En_CaseFilterType.CASE_TIME_ELAPSED)) {
            importanceContainer.addClassName(HIDE);
            stateContainer.addClassName(HIDE);
        } else {
            importanceContainer.removeClassName(HIDE);
            stateContainer.removeClassName(HIDE);
        }
    }

    public String validateMultiSelectorsTotalCount() {
        if (companies.getValue().size() > 50){
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
        }
        if (initiators.getValue().size() > 50){
            setInitiatorsErrorStyle(true);
            return lang.errTooMuchInitiators();
        } else {
            setManagersErrorStyle(false);
        }
        return null;
    }

    public boolean isSearchFieldCorrect(){
        return !searchByComments.getValue() ||
                search.getValue().length() >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS;
    }

    private void setCompaniesErrorStyle(boolean hasError) {
        if (hasError) {
            companies.addStyleName(REQUIRED);
        } else {
            companies.removeStyleName(REQUIRED);
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

    private void updateInitiators() {
        initiators.updateCompanies();
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
    RangePicker dateCreatedRange;
    @Inject
    @UiField(provided = true)
    RangePicker dateModifiedRange;
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
    EmployeeMultiSelector managers;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector commentAuthors;
    @Inject
    @UiField(provided = true)
    PersonMultiSelector creators;
    @Inject
    @UiField(provided = true)
    CaseTagMultiSelector tags;
    @UiField
    HTMLPanel searchPrivateContainer;
    @UiField
    ThreeStateButton searchPrivate;
    @UiField
    LabelElement labelCreated;
    @UiField
    LabelElement labelUpdated;
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


    @UiField
    DivElement modifiedRangeContainer;
    @UiField
    DivElement sortByContainer;

    @UiField
    DivElement importanceContainer;
    @UiField
    DivElement stateContainer;

    @Inject
    PolicyService policyService;

    private Timer timer = null;
    private AbstractIssueFilterModel model;

    interface IssueFilterUiBinder extends UiBinder<HTMLPanel, IssueFilterParamView> {}
    private static IssueFilterUiBinder ourUiBinder = GWT.create(IssueFilterUiBinder.class);
}
