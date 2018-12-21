package ru.protei.portal.ui.common.client.widget.issuefilter;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
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
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;

import java.util.Set;
import java.util.function.Supplier;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

public class IssueFilter extends Composite {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortField.setType(ModuleType.ISSUE);
        sortDir.setValue(false);
        dateRange.setPlaceholder(lang.selectDate());
    }

    public void setActivity(IssueFilterActivity activity) {
        this.activity = activity;
    }

    public HasValue<CaseFilterShortView> userFilter() {
        return userFilter;
    }

    public HasValue<String> searchPattern() {
        return search;
    }

    public HasValue<Boolean> searchByComments() {
        return searchByComments;
    }

    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    public HasValue<Set<PersonShortView>> initiators() {
        return initiators;
    }

    public HasValue<Set<PersonShortView>> managers() {
        return managers;
    }

    public HasValue<Set<PersonShortView>> commentAuthors() {
        return commentAuthors;
    }

    public HasValue<Boolean> searchPrivate() {
        return searchPrivate;
    }

    public HasValue<Set<En_ImportanceLevel>> importances() {
        return importance;
    }

    public HasValue<Set<En_CaseState>> states() {
        return state;
    }

    public HasVisibility productsVisibility() {
        return products;
    }

    public HasVisibility companiesVisibility() {
        return companies;
    }

    public HasVisibility managersVisibility() {
        return managers;
    }

    public HasVisibility commentAuthorsVisibility() {
        return commentAuthors;
    }

    public HasVisibility searchPrivateVisibility() {
        return searchPrivateContainer;
    }

    public HasVisibility searchByCommentsVisibility() {
        return searchByCommentsContainer;
    }

    public void resetFilter() {
        companies.setValue(null);
        products.setValue(null);
        managers.setValue(null);
        initiators.setValue(null);
        commentAuthors.setValue(null);
        importance.setValue(null);
        state.setValue(null);
        dateRange.setValue(null);
        sortField.setValue(En_SortField.creation_date);
        sortDir.setValue(false);
        search.setValue("");
        userFilter.setValue(null);
        searchByComments.setValue(false);
        searchPrivate.setValue(null);
        toggleMsgSearchThreshold();
    }

    public void fillFilterFields(CaseQuery params) {
        searchPattern().setValue(params.getSearchString());
        searchByComments().setValue(params.isSearchStringAtComments());
        searchPrivate().setValue(params.isViewPrivate());
        sortDir().setValue(params.getSortDir().equals(En_SortDir.ASC));
        sortField().setValue(params.getSortField());
        dateRange().setValue(new DateInterval(params.getFrom(), params.getTo()));
        importances().setValue(IssueFilterUtils.getImportances(params.getImportanceIds()));
        states().setValue(IssueFilterUtils.getStates(params.getStateIds()));
        companies().setValue(IssueFilterUtils.getCompanies(params.getCompanyIds()));
        updateInitiators();
        managers().setValue(IssueFilterUtils.getManagers(params.getManagerIds()));
        initiators().setValue(IssueFilterUtils.getInitiators(params.getInitiatorIds()));
        products().setValue(IssueFilterUtils.getProducts(params.getProductIds()));
        commentAuthors().setValue(IssueFilterUtils.getManagers(params.getCommentAuthorIds()));
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

    public void setCompaniesErrorStyle(boolean hasError) {
        if (hasError) {
            companies.addStyleName(REQUIRED);
        } else {
            companies.removeStyleName(REQUIRED);
        }
    }

    public void setProductsErrorStyle(boolean hasError) {
        if (hasError) {
            products.addStyleName(REQUIRED);
        } else {
            products.removeStyleName(REQUIRED);
        }
    }

    public void setManagersErrorStyle(boolean hasError) {
        if (hasError) {
            managers.addStyleName(REQUIRED);
        } else {
            managers.removeStyleName(REQUIRED);
        }
    }

    public void setInitiatorsErrorStyle(boolean hasError) {
        if (hasError) {
            initiators.addStyleName(REQUIRED);
        } else {
            initiators.removeStyleName(REQUIRED);
        }
    }

    public void setStateFilter(Selector.SelectorFilter<En_CaseState> caseStateFilter) {
        state.setFilter(caseStateFilter);
    }

    public void setInitiatorCompaniesSupplier(Supplier<Set<EntityOption>> collectionSupplier) {
        initiators.setCompaniesSupplier(collectionSupplier);
    }

    public void updateInitiators() {
        initiators.updateCompanies();
    }

    public void changeUserFilterValueName(CaseFilterShortView value) {
        userFilter.changeValueName(value );
    }

    public void addUserFilterDisplayOption(CaseFilterShortView value) {
        userFilter.addDisplayOption(value);
    }

    @UiHandler("userFilter")
    public void onKeyUpSearch(ValueChangeEvent<CaseFilterShortView> event) {
        if (activity != null) {
            activity.onUserFilterChanged();
        }
    }

    @UiHandler("search")
    public void onSearchChanged(ValueChangeEvent<String> event) {
        startFilterChangedTimer();
    }

    @UiHandler("searchByComments")
    public void onSearchByCommentsChanged(ValueChangeEvent<Boolean> event) {
        toggleMsgSearchThreshold();
        onFilterChanged();
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateInterval> event) {
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
        if (activity != null) {
            activity.onCompaniesFilterChanged();
        }
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

    private void ensureDebugIds() {
        userFilter.setEnsureDebugId(DebugIds.FILTER.USER_FILTER.FILTERS_BUTTON);
        search.setEnsureDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        search.setEnsureDebugIdAction(DebugIds.FILTER.SEARCH_CLEAR_BUTTON);
        searchByComments.setEnsureDebugId(DebugIds.FILTER.SEARCH_BY_COMMENTS_TOGGLE);
        dateRange.setEnsureDebugId(DebugIds.FILTER.DATE_RANGE_SELECTOR);
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

    private void onFilterChanged() {
        if (activity != null) {
            activity.onFilterChanged();
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

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    IssueFilterSelector userFilter;
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
    RangePicker dateRange;
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

    private Timer timer = null;
    private IssueFilterActivity activity = null;

    interface IssueFilterUiBinder extends UiBinder<HTMLPanel, IssueFilter> {}
    private static IssueFilterUiBinder ourUiBinder = GWT.create(IssueFilterUiBinder.class);
}
