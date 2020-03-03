package ru.protei.portal.ui.common.client.widget.issuefilter;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
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

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

public class IssueFilterParamView extends Composite implements AbstractIssueFilterWidgetView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortDir.setValue(false);
        dateCreatedRange.setPlaceholder(lang.selectDate());
        dateModifiedRange.setPlaceholder(lang.selectDate());
        searchByCommentsWarning.setText(lang.searchByCommentsUnavailable(CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS));
    }

    @Override
    public void setActivity(AbstractIssueFilterParamActivity activity) {
        this.activity = activity;
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
    public AbstractIssueFilterParamActivity getActivity() {
        return activity;
    }

    @Override
    public HasValue<CaseFilterShortView> userFilter() {
        return userFilter;
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
    public void presetFilterType() {
        userFilter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
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
        userFilter.setValue(null);
        searchByComments.setValue(false);
        searchPrivate.setValue(null);
        tags.setValue(null);
        tags.isProteiUser( policyService.hasSystemScopeForPrivilege( En_Privilege.ISSUE_VIEW ) );
    }

    @Override
    public void fillFilterFields(CaseQuery caseQuery, SelectorsParams filter) {
        searchPattern().setValue(caseQuery.getSearchString());
        searchByComments().setValue(caseQuery.isSearchStringAtComments());
        searchPrivate().setValue(caseQuery.isViewPrivate());
        sortDir().setValue(caseQuery.getSortDir().equals(En_SortDir.ASC));
        sortField().setValue(caseQuery.getSortField());
        dateCreatedRange().setValue(new DateInterval(caseQuery.getCreatedFrom(), caseQuery.getCreatedTo()));
        dateModifiedRange().setValue(new DateInterval(caseQuery.getModifiedFrom(), caseQuery.getModifiedTo()));
        importances().setValue(IssueFilterUtils.getImportances(caseQuery.getImportanceIds()));
        states().setValue(IssueFilterUtils.getStates(caseQuery.getStateIds()));

        companies().setValue(new HashSet<>(emptyIfNull(filter.getCompanyEntityOptions())));
        updateInitiators();

        initiators().setValue(applyPersons(filter, caseQuery.getInitiatorIds()));
        commentAuthors().setValue(applyPersons(filter, caseQuery.getCommentAuthorIds()));
        creators().setValue(applyPersons(filter, caseQuery.getCreatorIds()));

        Set<PersonShortView> personShortViews = new LinkedHashSet<>();
        if (emptyIfNull(caseQuery.getManagerIds()).contains(CrmConstants.Employee.UNDEFINED)) {
            personShortViews.add(new PersonShortView(lang.employeeWithoutManager(), CrmConstants.Employee.UNDEFINED));
        }
        personShortViews.addAll(applyPersons(filter, caseQuery.getManagerIds()));
        managers().setValue(personShortViews);

        Set<ProductShortView> products = new LinkedHashSet<>();
        if (emptyIfNull(caseQuery.getProductIds()).contains(CrmConstants.Product.UNDEFINED)) {
            products.add(new ProductShortView(CrmConstants.Product.UNDEFINED, lang.productWithout(), 0));
        }
        products.addAll(emptyIfNull(filter.getProductShortViews()));
        products().setValue(products);

        tags().setValue(setOf( filter.getCaseTags() ) );
    }

    @Override
    public void setCompaniesErrorStyle(boolean hasError) {
        if (hasError) {
            companies.addStyleName(REQUIRED);
        } else {
            companies.removeStyleName(REQUIRED);
        }
    }

    @Override
    public void setProductsErrorStyle(boolean hasError) {
        if (hasError) {
            products.addStyleName(REQUIRED);
        } else {
            products.removeStyleName(REQUIRED);
        }
    }

    @Override
    public void setManagersErrorStyle(boolean hasError) {
        if (hasError) {
            managers.addStyleName(REQUIRED);
        } else {
            managers.removeStyleName(REQUIRED);
        }
    }

    @Override
    public void setInitiatorsErrorStyle(boolean hasError) {
        if (hasError) {
            initiators.addStyleName(REQUIRED);
        } else {
            initiators.removeStyleName(REQUIRED);
        }
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<En_CaseState> caseStateFilter) {
        state.setFilter(caseStateFilter);
    }

    @Override
    public void setInitiatorCompaniesSupplier(Supplier<Set<EntityOption>> collectionSupplier) {
        initiators.setCompaniesSupplier(collectionSupplier);
    }

    @Override
    public void updateInitiators() {
        initiators.updateCompanies();
    }

    @Override
    public void changeUserFilterValueName(CaseFilterShortView value) {
        //userFilter.changeValueName(value );
    }

    @Override
    public void addUserFilterDisplayOption(CaseFilterShortView value) {
        //userFilter.addDisplayOption(value);
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
        userFilter.watchForScrollOf(widget);
        sortField.watchForScrollOf(widget);
    }

    public void stopWatchForScrollOf(Widget widget) {
        userFilter.stopWatchForScrollOf(widget);
        sortField.stopWatchForScrollOf(widget);
    }

    private Set<PersonShortView> applyPersons(SelectorsParams filter, List<Long> personIds) {
        return emptyIfNull(filter.getPersonShortViews()).stream()
                .filter(personShortView ->
                        emptyIfNull(personIds).stream().anyMatch(ids -> ids.equals(personShortView.getId())))
                .collect(Collectors.toSet());
    }

    private void ensureDebugIds() {
        userFilter.setEnsureDebugId(DebugIds.FILTER.USER_FILTER.FILTERS_BUTTON);
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
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    private void startFilterChangedTimer() {
        if (timer == null) {
            timer = new Timer() {
                @Override
                public void run() {
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

    @Inject
    PolicyService policyService;

    private Timer timer = null;
    private AbstractIssueFilterParamActivity activity = null;

    interface IssueFilterUiBinder extends UiBinder<HTMLPanel, IssueFilterParamView> {}
    private static IssueFilterUiBinder ourUiBinder = GWT.create(IssueFilterUiBinder.class);
}
