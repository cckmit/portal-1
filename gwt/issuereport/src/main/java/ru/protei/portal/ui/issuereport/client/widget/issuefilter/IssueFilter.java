package ru.protei.portal.ui.issuereport.client.widget.issuefilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
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
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilterModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.*;

public class IssueFilter extends Composite implements HasValue<CaseQuery>, AbstractIssueFilter {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortDir.setValue(false);
        dateCreatedRange.setPlaceholder(lang.selectDate());
        dateModifiedRange.setPlaceholder(lang.selectDate());
        initiators.setCompaniesSupplier(() -> companies.getValue());
    }

    @Override
    public void setModel(AbstractIssueFilterModel model) {
        this.model = model;
    }

    public void updateFilterType(En_CaseFilterType filterType) {
        this.filterType = filterType;
        reset();
        userFilter.updateFilterType(filterType);
        applyVisibilityByFilterType();
    }

    @Override
    public void reset() {
        companies.setValue(null);
        products.setValue(null);
        managers.setValue(null);
        initiators.setValue(null);
        commentAuthors.setValue(null);
        importance.setValue(null);
        state.setValue(null);
        dateCreatedRange.setValue(null);
        dateModifiedRange.setValue(null);
        sortField.setValue(En_SortField.issue_number);
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
        setUserFilterNameVisibility(false);
        if (filterType != null && filterType.equals(En_CaseFilterType.CASE_RESOLUTION_TIME)) {
            state.setValue(new HashSet<>(activeStates));
        }
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

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseQuery> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
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
    public HasVisibility searchPrivateVisibility() {
        return searchPrivateContainer;
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public void updateInitiators() {
        initiators.updateCompanies();
    }

    @UiHandler("userFilter")
    public void onUserFilterChanged(ValueChangeEvent<CaseFilterShortView> event) {
        if (model == null) {
            return;
        }
        CaseFilterShortView value = userFilter.getValue();
        if (value == null || value.getId() == null) {
            reset();
            onIssueFilterChanged();
            return;
        }

        model.onUserFilterChanged(value.getId(), caseFilter -> {
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
        toggleMsgSearchThreshold();
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
        reset();
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
    public void onOkBtnClicked(ClickEvent event) {
        event.preventDefault();

        if (model == null) {
            return;
        }

        if (filterName.getValue().isEmpty()){
            setFilterNameContainererroryle(true);
            return;
        }

        CaseFilter userFilter = fillUserFilter();
        if (!isCreateFilterAction){
            userFilter.setId(this.userFilter.getValue().getId());
        }

        model.onSaveFilterClicked(userFilter, caseFilterShortView -> {
            this.userFilter.setValue(caseFilterShortView);
            showUserFilterControls();
        });
    }

    @UiHandler("cancelBtn")
    public void onCancelBtnClicked(ClickEvent event) {
        event.preventDefault();
        showUserFilterControls();
        if (userFilter.getValue() == null) {
            removeBtn.setVisible(false);
            saveBtn.setVisible(false);
        }
    }

    @UiHandler("removeBtn")
    public void onRemoveClicked(ClickEvent event) {
        if (model == null) {
            return;
        }
        CaseFilterShortView value = userFilter.getValue();
        if (value == null || value.getId() == null) {
            return;
        }
        model.onRemoveFilterClicked(value.getId());
    }

    private void applyVisibilityByFilterType() {
        if (filterType == null) {
            return;
        }

        search.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        searchByComments.setVisible(filterType.equals(En_CaseFilterType.CASE_OBJECTS));
        if (filterType.equals(En_CaseFilterType.CASE_OBJECTS)) {
            modifiedRangeContainer.removeClassName(HIDE);
            sortByContainer.removeClassName(HIDE);
            dateLabel.setInnerText(lang.created());
        } else {
            modifiedRangeContainer.addClassName(HIDE);
            sortByContainer.addClassName(HIDE);
            dateLabel.setInnerText(lang.period());
        }
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

    private void setFilterNameContainererroryle(boolean hasError) {
        if (hasError) {
            filterName.addStyleName(REQUIRED);
        } else {
            filterName.removeStyleName(REQUIRED);
        }
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

    private void showUserFilterName(){
        setUserFilterControlsVisibility(false);
        setUserFilterNameVisibility(true);
    }

    private void showUserFilterControls() {
        setUserFilterControlsVisibility(true);
        setUserFilterNameVisibility(false);
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

    private void fillFilterFields(CaseQuery caseQuery) {
        search.setValue(caseQuery.getSearchString());
        searchByComments.setValue(caseQuery.isSearchStringAtComments());
        searchPrivate.setValue(caseQuery.isViewPrivate());
        sortDir.setValue(caseQuery.getSortDir() == null ? null : caseQuery.getSortDir().equals(En_SortDir.ASC));
        sortField.setValue(caseQuery.getSortField() == null ? En_SortField.creation_date : caseQuery.getSortField());
        dateCreatedRange.setValue(new DateInterval(caseQuery.getCreatedFrom(), caseQuery.getCreatedTo()));
        dateModifiedRange.setValue(new DateInterval(caseQuery.getModifiedFrom(), caseQuery.getModifiedTo()));
        importance.setValue(getImportances(caseQuery.getImportanceIds()));
        state.setValue(getStates(caseQuery.getStateIds()));
        companies.setValue(getCompanies(caseQuery.getCompanyIds()));
        updateInitiators();
        managers.setValue(getPersons(caseQuery.getManagerIds()));
        initiators.setValue(getPersons(caseQuery.getInitiatorIds()));
        products.setValue(getProducts(caseQuery.getProductIds()));
        commentAuthors.setValue(getPersons(caseQuery.getCommentAuthorIds()));
        tags.setValue(getOptions(caseQuery.getCaseTagsIds()));
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

        if (filterType == null) {
            return null;
        }

        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.CRM_SUPPORT);

        switch (filterType) {
            case CASE_OBJECTS: {
                if (isFillSearchString) {
                    String searchString = search.getValue();
                    query.setCaseNumbers(searchCaseNumber(searchString, searchByComments.getValue()));
                    if (query.getCaseNumbers() == null) {
                        query.setSearchStringAtComments(searchByComments.getValue());
                        query.setSearchString(isBlank(searchString) ? null : searchString);
                    }
                }
                query.setViewPrivate(searchPrivate.getValue());
                query.setSortField(sortField.getValue());
                query.setSortDir(sortDir.getValue() ? En_SortDir.ASC : En_SortDir.DESC);
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setManagerIds(getManagersIdList(managers.getValue()));
                query.setInitiatorIds(getManagersIdList(initiators.getValue()));
                query.setImportanceIds(getImportancesIdList(importance.getValue()));
                query.setStates(getStateList(state.getValue()));
                query.setCommentAuthorIds(getManagersIdList(commentAuthors.getValue()));
                query.setCaseTagsIds(getIds(tags.getValue()));
                query = fillCreatedInterval(query, dateCreatedRange.getValue());
                query = fillModifiedInterval(query, dateModifiedRange.getValue());
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
                query.setCaseTagsIds(getIds(tags.getValue()));
                query.setImportanceIds(getImportancesIdList(importance.getValue()));
                query.setStates(getStateList(state.getValue()));
                query = fillCreatedInterval(query, dateCreatedRange.getValue());
                break;
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
    @UiField
    DivElement modifiedRangeContainer;
    @UiField
    DivElement sortByContainer;
    @UiField
    DivElement importanceContainer;
    @UiField
    DivElement stateContainer;
    @UiField
    LabelElement dateLabel;

    private AbstractIssueFilterModel model;
    private Timer timer = null;
    private boolean isCreateFilterAction = true;
    private En_CaseFilterType filterType = En_CaseFilterType.CASE_OBJECTS;
    private Set<En_CaseState> activeStates = new HashSet<>(Arrays.asList(En_CaseState.CREATED, En_CaseState.OPENED,
            En_CaseState.ACTIVE, En_CaseState.TEST_LOCAL, En_CaseState.WORKAROUND));

    private static IssueFilterUiBinder ourUiBinder = GWT.create( IssueFilterUiBinder.class );
    interface IssueFilterUiBinder extends UiBinder< HTMLPanel, IssueFilter > {}
}