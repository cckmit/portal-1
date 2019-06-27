package ru.protei.portal.ui.issuereport.client.widget.issuefilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
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

public class IssueFilter extends Composite implements HasValue<CaseFilter> {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortField.setType( ModuleType.ISSUE);
        sortDir.setValue(false);
        dateCreatedRange.setPlaceholder(lang.selectDate());
        dateModifiedRange.setPlaceholder(lang.selectDate());
        userFilter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
    }

    @Override
    public CaseFilter getValue() {
        return fillUserFilter();
    }

    @Override
    public void setValue(CaseFilter value) {
        if (value == null) return;
        fillFilterFields(value.getParams());
    }

    @Override
    public void setValue(CaseFilter value, boolean fireEvents) {

    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler< CaseFilter > handler) {
        return null;
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

    public void updateInitiators() {
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
        CaseQuery query = new CaseQuery();/*IssueFilterUtils.makeCaseQuery(filterParamView, false);*/
        filter.setParams(query);
        query.setSearchString(search.getValue());
        return filter;
    }

    private void onFilterChanged() {
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

    private Timer timer = null;

    private static IssueFilterUiBinder ourUiBinder = GWT.create( IssueFilterUiBinder.class );
    interface IssueFilterUiBinder extends UiBinder< HTMLPanel, IssueFilter > {}
}