package ru.protei.portal.ui.issuereport.client.widget.issuefilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
import ru.protei.portal.ui.common.client.widget.issueimportance.btngroup.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.issuestate.optionlist.IssueStatesOptionList;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;
import ru.protei.portal.ui.common.client.widget.selector.casetag.CaseTagMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilter;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilterActivity;

import java.util.Set;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

public class IssueFilter extends Composite implements HasValue<CaseQuery>, HasValueChangeHandlers<CaseQuery>, AbstractIssueFilter {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortField.setType(ModuleType.ISSUE);
        sortDir.setValue(false);
        dateCreatedRange.setPlaceholder(lang.selectDate());
        dateModifiedRange.setPlaceholder(lang.selectDate());
        initiators.setCompaniesSupplier(() -> companies.getValue());
    }

    @Override
    public void setActivity( AbstractIssueFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        companies.setValue(null);
        products.setValue(null);
        managers.setValue(null);
        initiators.setValue(null);
        commentAuthors.setValue(null);
        importance.setValue(null);
        state.setValue(null);
        dateCreatedRange.setValue(null);
        dateModifiedRange.setValue(null);
        sortField.setValue(En_SortField.creation_date);
        sortDir.setValue(false);
        search.setValue("");
        userFilter.setValue(null);
        searchByComments.setValue(false);
        searchPrivate.setValue(null);
        tags.setValue(null);
        toggleMsgSearchThreshold();
        removeBtn.setVisible(false);
        saveBtn.setVisible(false);
        createBtn.setVisible(true);
        resetBtn.setVisible(true);
        filterName.removeStyleName(REQUIRED);
        filterName.setValue("");
        showUserFilterControls();
    }

    @Override
    public CaseQuery getValue() {
        return makeCaseQuery(true);
    }

    @Override
    public void setValue(CaseQuery value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CaseQuery value, boolean fireEvents) {
        if (value == null) {
            return;
        }

        fillFilterFields(value);
        toggleMsgSearchThreshold();

        if (fireEvents) {
            ValueChangeEvent.fire(this, getValue());
        }
    }

    public void updateFilterType(En_CaseFilterType filterType) {
        this.filterType = filterType;
        resetFilter();
        userFilter.updateFilterType(filterType);
    }

    public HasVisibility productsVisibility() {
        return products;
    }

    @Override
    public HasVisibility companiesVisibility() {
        return companies;
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
    public HasVisibility tagsVisibility() {
        return tags;
    }

    @Override
    public HasVisibility searchPrivateVisibility() {
        return searchPrivateContainer;
    }

    @Override
    public HasVisibility searchByCommentsVisibility() {
        return searchByCommentsContainer;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseQuery> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("userFilter")
    public void onUserFilterChanged(ValueChangeEvent<CaseFilterShortView> event) {
        if (activity == null) {
            return;
        }
        CaseFilterShortView value = userFilter.getValue();
        if (value == null || value.getId() == null) {
            resetFilter();
            onIssueFilterChanged();
            return;
        }

        activity.onUserFilterChanged(value.getId(), caseFilter -> {
            setValue(caseFilter.getParams());
            filterName.setValue(caseFilter.getName());
            showUserFilterControls();
        });
    }

    @UiHandler("search")
    public void onSearchChanged(ValueChangeEvent<String> event) {
        startFilterChangedTimer();
    }

    @UiHandler("searchByComments")
    public void onSearchByCommentsChanged(ValueChangeEvent<Boolean> event) {
        onIssueFilterChanged();
    }

    @UiHandler("dateCreatedRange")
    public void onDateCreatedRangeChanged(ValueChangeEvent<DateInterval> event) {
        onIssueFilterChanged();
    }

    @UiHandler("dateModifiedRange")
    public void onDateModifiedRangeChanged(ValueChangeEvent<DateInterval> event) {
        onIssueFilterChanged();
    }

    @UiHandler("sortField")
    public void onSortFieldSelected(ValueChangeEvent<En_SortField> event) {
        onIssueFilterChanged();
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event) {
        onIssueFilterChanged();
    }

    @UiHandler("products")
    public void onProductsSelected(ValueChangeEvent<Set<ProductShortView>> event) {
        onIssueFilterChanged();
    }

    @UiHandler("companies")
    public void onCompaniesSelected(ValueChangeEvent<Set<EntityOption>> event) {
        updateInitiators();
        onIssueFilterChanged();
    }

    @UiHandler("initiators")
    public void onInitiatorsSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onIssueFilterChanged();
    }

    @UiHandler("managers")
    public void onManagersSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onIssueFilterChanged();
    }

    @UiHandler("commentAuthors")
    public void onCommentAuthorsSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onIssueFilterChanged();
    }

    @UiHandler("tags")
    public void onTagsSelected(ValueChangeEvent<Set<EntityOption>> event) {
        onIssueFilterChanged();
    }

    @UiHandler("searchPrivate")
    public void onSearchOnlyPrivateChanged(ValueChangeEvent<Boolean> event) {
        onIssueFilterChanged();
    }

    @UiHandler("importance")
    public void onImportanceSelected(ValueChangeEvent<Set<En_ImportanceLevel>> event) {
        onIssueFilterChanged();
    }

    @UiHandler("state")
    public void onStateSelected(ValueChangeEvent<Set<En_CaseState>> event) {
        onIssueFilterChanged();
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked(ClickEvent event) {
        resetFilter();
        onIssueFilterChanged();
    }

    @UiHandler("saveBtn")
    public void onSaveClicked(ClickEvent event) {
        isCreateFilterAction = false;
        showUserFilterName();
    }

    @UiHandler("createBtn")
    public void onCreateClicked(ClickEvent event) {
        isCreateFilterAction = true;
        showUserFilterName();
    }

    @UiHandler("okBtn")
    public void onOkBtnClicked( ClickEvent event ) {
        event.preventDefault();

        if ( activity == null) {
            return;
        }

        if (filterName.getValue().isEmpty()){
            setFilterNameContainerErrorStyle(true);
            return;
        }
        CaseFilter userFilter = fillUserFilter();
        if (!isCreateFilterAction){
            userFilter.setId(this.userFilter.getValue().getId());
        }

        activity.onSaveFilterClicked(userFilter, caseFilterShortView -> {
            this.userFilter.setValue(caseFilterShortView);
            showUserFilterControls();
        });
    }

    @UiHandler("cancelBtn")
    public void onCancelBtnClicked(ClickEvent event) {
        event.preventDefault();
        showUserFilterControls();
    }

    @UiHandler("removeBtn")
    public void onRemoveClicked (ClickEvent event) {
        if (activity == null) {
            return;
        }
        CaseFilterShortView value = userFilter.getValue();
        if (value == null || value.getId() == null) {
            return;
        }
        activity.onRemoveFilterClicked(value.getId());
    }

    private void setUserFilterNameVisibility(boolean hasVisible) {
        if (hasVisible) {
            filterNameContainer.removeClassName(HIDE);
        } else {
            filterNameContainer.addClassName(HIDE);
        }
    }

    private void setUserFilterControlsVisibility(boolean hasVisible) {
        createBtn.setVisible(hasVisible);
        saveBtn.setVisible(hasVisible);
        resetBtn.setVisible(hasVisible);
        removeBtn.setVisible(hasVisible);
    }

    public void setFilterNameContainerErrorStyle(boolean hasError) {
        if (hasError) {
            filterName.addStyleName(REQUIRED);
        } else {
            filterName.removeStyleName(REQUIRED);
        }
    }

    public void toggleMsgSearchThreshold() {
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

    private void updateInitiators() {
        initiators.updateCompanies();
    }

    private void fillFilterFields(CaseQuery caseQuery) {
        search.setValue(caseQuery.getSearchString());
        searchByComments.setValue(caseQuery.isSearchStringAtComments());
        searchPrivate.setValue(caseQuery.isViewPrivate());
        sortDir.setValue(caseQuery.getSortDir().equals( En_SortDir.ASC));
        sortField.setValue(caseQuery.getSortField());
        dateCreatedRange.setValue(new DateInterval(caseQuery.getCreatedFrom(), caseQuery.getCreatedTo()));
        dateModifiedRange.setValue(new DateInterval(caseQuery.getModifiedFrom(), caseQuery.getModifiedTo()));
        importance.setValue( IssueFilterUtils.getImportances(caseQuery.getImportanceIds()));
        state.setValue(IssueFilterUtils.getStates(caseQuery.getStateIds()));
        companies.setValue(IssueFilterUtils.getCompanies(caseQuery.getCompanyIds()));
        updateInitiators();
        managers.setValue(IssueFilterUtils.getPersons(caseQuery.getManagerIds()));
        initiators.setValue(IssueFilterUtils.getPersons(caseQuery.getInitiatorIds()));
        products.setValue(IssueFilterUtils.getProducts(caseQuery.getProductIds()));
        commentAuthors.setValue(IssueFilterUtils.getPersons(caseQuery.getCommentAuthorIds()));
        tags.setValue(IssueFilterUtils.getOptions(caseQuery.getCaseTagsIds()));
    }

    private CaseFilter fillUserFilter() {
        CaseFilter filter = new CaseFilter();
        filter.setName(filterName.getValue());
        filter.setType(filterType);
        CaseQuery query = makeCaseQuery(false);
        filter.setParams(query);
        query.setSearchString(search.getValue());
        return filter;
    }

    private void onIssueFilterChanged() {
        ValueChangeEvent.fire(this, getValue());
    }

    private void startFilterChangedTimer() {
        if (timer == null) {
            timer = new Timer() {
                @Override
                public void run() {
                    toggleMsgSearchThreshold();
                    onIssueFilterChanged();
                }
            };
        } else {
            timer.cancel();
        }
        timer.schedule(300);
    }

    private CaseQuery makeCaseQuery(boolean isFillSearchString) {
        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.CRM_SUPPORT);
        if (isFillSearchString) {
            String searchString = search.getValue();
            query.setCaseNumbers(IssueFilterUtils.searchCaseNumber(searchString, searchByComments.getValue()));
            if (query.getCaseNumbers() == null) {
                query.setSearchStringAtComments(searchByComments.getValue());
                query.setSearchString(isBlank(searchString) ? null : searchString);
            }
        }
        query.setViewPrivate(searchPrivate.getValue());
        query.setSortField(sortField.getValue());
        query.setSortDir(sortDir.getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setCompanyIds(IssueFilterUtils.getCompaniesIdList(companies.getValue()));
        query.setProductIds(IssueFilterUtils.getProductsIdList(products.getValue()));
        query.setManagerIds(IssueFilterUtils.getManagersIdList(managers.getValue()));
        query.setInitiatorIds(IssueFilterUtils.getManagersIdList(initiators.getValue()));
        query.setImportanceIds(IssueFilterUtils.getImportancesIdList(importance.getValue()));
        query.setStates(IssueFilterUtils.getStateList(state.getValue()));
        query.setCommentAuthorIds(IssueFilterUtils.getManagersIdList(commentAuthors.getValue()));
        query.setCaseTagsIds(IssueFilterUtils.getIds(tags.getValue()));
        DateInterval createdInterval = dateCreatedRange.getValue();
        if (createdInterval != null) {
            query.setCreatedFrom(createdInterval.from);
            query.setCreatedTo(createdInterval.to);
        }
        DateInterval modifiedInterval = dateModifiedRange.getValue();
        if (modifiedInterval != null) {
            query.setModifiedFrom(modifiedInterval.from);
            query.setModifiedTo(modifiedInterval.to);
        }
        return query;
    }

    private void ensureDebugIds() {
        userFilter.setEnsureDebugId(DebugIds.FILTER.USER_FILTER.FILTERS_BUTTON);
        search.setEnsureDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        search.setEnsureDebugIdAction(DebugIds.FILTER.SEARCH_CLEAR_BUTTON);
        searchByComments.setEnsureDebugId(DebugIds.FILTER.SEARCH_BY_COMMENTS_TOGGLE);
        dateCreatedRange.setEnsureDebugId(DebugIds.FILTER.DATE_RANGE_SELECTOR);
        dateModifiedRange.setEnsureDebugId(DebugIds.FILTER.DATE_RANGE_SELECTOR);
        sortField.setEnsureDebugId(DebugIds.FILTER.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.FILTER.SORT_DIR_BUTTON);
        companies.setAddEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_ADD_BUTTON);
        companies.setClearEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_CLEAR_BUTTON);
        products.setAddEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_ADD_BUTTON);
        products.setClearEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_CLEAR_BUTTON);
        managers.setAddEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_ADD_BUTTON);
        managers.setClearEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_CLEAR_BUTTON);
        initiators.setAddEnsureDebugId(DebugIds.FILTER.INITIATORS_SELECTOR_ADD_BUTTON);
        initiators.setClearEnsureDebugId(DebugIds.FILTER.INITIATORS_SELECTOR_CLEAR_BUTTON);
        searchPrivate.setYesEnsureDebugId(DebugIds.FILTER.PRIVACY_YES_BUTTON);
        searchPrivate.setNotDefinedEnsureDebugId(DebugIds.FILTER.PRIVACY_NOT_DEFINED_BUTTON);
        searchPrivate.setNoEnsureDebugId(DebugIds.FILTER.PRIVACY_NO_BUTTON);
    }

    private void showUserFilterName(){
        setUserFilterControlsVisibility(false);
        setUserFilterNameVisibility(true);
    }

    private void showUserFilterControls() {
        setUserFilterControlsVisibility(true);
        setUserFilterNameVisibility(false);
    }

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    IssueFilterSelector userFilter;
    @UiField
    HTMLPanel body;
    @UiField
    CleanableSearchBox search;
    @UiField
    HTMLPanel searchByCommentsContainer;
    @UiField
    Label searchByCommentsWarning;
    @UiField
    OptionItem searchByComments;
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
    InitiatorMultiSelector initiators;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector managers;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector commentAuthors;
    @Inject
    @UiField(provided = true)
    CaseTagMultiSelector tags;
    @UiField
    HTMLPanel searchPrivateContainer;
    @UiField
    ThreeStateButton searchPrivate;
    @Inject
    @UiField(provided = true)
    ImportanceBtnGroupMulti importance;
    @Inject
    @UiField(provided = true)
    IssueStatesOptionList state;

    @UiField
    Button resetBtn;
    @UiField
    Button createBtn;
    @UiField
    Button saveBtn;
    @UiField
    Button removeBtn;
    @UiField
    Anchor okBtn;
    @UiField
    Anchor cancelBtn;
    @UiField
    TextBox filterName;
    @UiField
    DivElement filterNameContainer;


    private Timer timer = null;
    private AbstractIssueFilterActivity activity;
    private boolean isCreateFilterAction = true;
    private En_CaseFilterType filterType;

    private static IssueFilterUiBinder ourUiBinder = GWT.create( IssueFilterUiBinder.class );
    interface IssueFilterUiBinder extends UiBinder< HTMLPanel, IssueFilter > {}
}