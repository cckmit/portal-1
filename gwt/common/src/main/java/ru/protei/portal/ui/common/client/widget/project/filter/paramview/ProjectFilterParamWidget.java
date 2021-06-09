package ru.protei.portal.ui.common.client.widget.project.filter.paramview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterParamView;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.state.ProjectStateBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.makeSearchStringFromCaseNumber;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.searchCaseNumber;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.fromDateRange;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

public class ProjectFilterParamWidget extends Composite implements FilterParamView<ProjectQuery> {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        sortField.setType( ModuleType.PROJECT );
        fillDateRanges(commentCreationRange);
        ensureDebugIds();
    }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.project_name );
        sortDir.setValue( true );
        search.setValue( "" );
        direction.setValue( new HashSet<>() );
        states.setValue( new HashSet<>() );
        regions.setValue(new HashSet<>());
        headManagers.setValue(new HashSet<>());
        caseMembers.setValue(new HashSet<>());
        onlyMineProjects.setValue( false );
        initiatorCompanies.setValue( null );
        products.setValue(new HashSet<>());
        commentCreationRange.setValue(null);
        if (isAttached()) {
            onFilterChanged();
        }
    }

    @Override
    public ProjectQuery getQuery() {
        ProjectQuery query = new ProjectQuery();

        String searchString = search.getValue();
        query.setCaseIds(searchCaseNumber(searchString, false));
        if (query.getCaseIds() == null) {
            query.setSearchString(isBlank(searchString) ? null : searchString);
        }

        query.setStateIds(toSet(states.getValue(), CaseState::getId));
        query.setRegionIds(toSet(regions.getValue(), EntityOption::getId));
        query.setHeadManagerIds(toSet(headManagers.getValue(), PersonShortView::getId));
        query.setCaseMemberIds(toSet(caseMembers.getValue(), PersonShortView::getId));
        query.setDirectionIds(toSet(direction.getValue(), ProductDirectionInfo::getId));
        query.setSortField(sortField.getValue());
        query.setSortDir(sortDir.getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        if(onlyMineProjects.getValue() != null && onlyMineProjects.getValue()) {
            query.setMemberId(policyService.getProfile().getId());
        }
        query.setInitiatorCompanyIds(initiatorCompanies.getValue().stream().map(EntityOption::getId).collect(Collectors.toSet()));
        query.setProductIds(products.getValue().stream().map(ProductShortView::getId).collect(Collectors.toSet()));
        query.setCommentCreationRange(toDateRange(commentCreationRange.getValue()));
        return query;
    }

    @Override
    public void fillFilterFields(ProjectQuery query, SelectorsParams selectorsParams) {
        if (query.getCaseIds() != null) {
            search.setValue(makeSearchStringFromCaseNumber(query.getCaseIds()));
        } else {
            search.setValue(query.getSearchString());
        }

        if (isNotEmpty(query.getStateIds())) {
            states.setValue(toSet(query.getStateIds(), (Function<Long, CaseState>) CaseState::new));
        }

        Set<EntityOption> regions = collectRegions(selectorsParams.getRegions(), query.getRegionIds());
        if (emptyIfNull(query.getRegionIds()).contains(CrmConstants.Region.UNDEFINED)) {
            regions.add(new EntityOption(lang.regionNotSpecified(), CrmConstants.Product.UNDEFINED));
        }

        this.regions.setValue(regions);

        Set<PersonShortView> headManagers = collectPersons(selectorsParams.getPersonShortViews(), query.getHeadManagerIds());
        this.headManagers.setValue(headManagers);

        Set<PersonShortView> caseMembers = collectPersons(selectorsParams.getPersonShortViews(), query.getCaseMemberIds());
        this.caseMembers.setValue(caseMembers);

        Set<ProductDirectionInfo> directions = collectDirections(selectorsParams.getProductDirectionInfos(), query.getDirectionIds());
        if (emptyIfNull(query.getDirectionIds()).contains(CrmConstants.Product.UNDEFINED)) {
            directions.add(new ProductDirectionInfo(CrmConstants.Product.UNDEFINED, lang.productDirectionNotSpecified()));
        }
        direction.setValue(directions);

        sortField.setValue(query.getSortField());
        sortDir.setValue(query.getSortDir() == En_SortDir.ASC);
        onlyMineProjects.setValue(query.getMemberId() != null);
        commentCreationRange.setValue(fromDateRange(query.getCommentCreationRange()));
        initiatorCompanies.setValue(
                stream(query.getInitiatorCompanyIds()).map(EntityOption::new).collect(Collectors.toSet()));

        Set<EntityOption> initiatorsCompanies = collectCompanies(selectorsParams.getCompanyEntityOptions(), query.getInitiatorCompanyIds());
        initiatorCompanies.setValue(initiatorsCompanies);

        Set<ProductShortView> products = collectProducts(selectorsParams.getProductShortViews(), query.getProductIds());
        this.products.setValue(products);

        if (validate()) {
            onFilterChanged();
        }
    }

    public HasVisibility onlyMineProjectsVisibility() {
        return onlyMineProjects;
    }

    public void setCommentCreationRangeValid(boolean isTypeValid, boolean isRangeValid) {
        commentCreationRange.setValid(isTypeValid, isRangeValid);
    }

    @Override
    public void setValidateCallback(Consumer<Boolean> callback) {
        this.validateCallback = callback;
    }

    @Override
    public void setOnFilterChangeCallback(Runnable callback) {
        this.filterChangeCallback = callback;
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent<En_SortField> event ) {
        onFilterChanged();
    }

    @UiHandler( "states" )
    public void onStateSelected( ValueChangeEvent<Set<CaseState>> event ) {
        onFilterChanged();
    }

    @UiHandler( "direction" )
    public void onDirectionSelected( ValueChangeEvent<Set<ProductDirectionInfo>> event ) {
        onFilterChanged();
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event ) {
        onFilterChanged();
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        changeTimer.schedule(300);
    }

    @UiHandler( "onlyMineProjects" )
    public void onOnlyMineProjectsChanged( ValueChangeEvent<Boolean> event ) {
        onFilterChanged();
    }

    @UiHandler( "regions" )
    public void onRegionSelected( ValueChangeEvent<Set<EntityOption>> event ) {
        onFilterChanged();
    }

    @UiHandler( "headManagers" )
    public void onHeadManagerSelected(ValueChangeEvent<Set<PersonShortView>> event ) {
        onFilterChanged();
    }

    @UiHandler( "caseMembers" )
    public void onCaseMemberSelected( ValueChangeEvent<Set<PersonShortView>> event ) {
        onFilterChanged();
    }

    @UiHandler( "initiatorCompanies" )
    public void onInitiatorCompaniesSelected( ValueChangeEvent<Set<EntityOption>> event ) {
        onFilterChanged();
    }

    @UiHandler( "products" )
    public void onProductsSelected( ValueChangeEvent<Set<ProductShortView>> event ) {
        onFilterChanged();
    }

    @UiHandler("commentCreationRange")
    public void onDateRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        if (validate()) {
            onFilterChanged();
        }
    }

    private boolean validate() {
        boolean isValid = isCommentCreationRangeValid() && isCommentCreationRangeTypeValid();

        if (validateCallback != null) {
            validateCallback.accept(isValid);
        }

        return isDateRangeValid(commentCreationRange.getValue());
    }

    private void onFilterChanged() {
        if (filterChangeCallback != null) {
            filterChangeCallback.run();
        }
    }

    private boolean isCommentCreationRangeTypeValid() {
        return !commentCreationRange.isTypeMandatory()
                || (commentCreationRange.getValue() != null
                && commentCreationRange.getValue().getIntervalType() != null);
    }

    private boolean isCommentCreationRangeValid() {
        return isDateRangeValid(commentCreationRange.getValue());
    }

    private void fillDateRanges(TypedSelectorRangePicker rangePicker) {
        rangePicker.fillSelector(En_DateIntervalType.issueTypes());
    }

    public boolean isDateRangeValid(DateIntervalWithType dateRange) {
        if (dateRange == null || dateRange.getIntervalType() == null) {
            return true;
        }

        return !Objects.equals(dateRange.getIntervalType(), En_DateIntervalType.FIXED) || dateRange.getInterval().isValid();
    }

    private Set<EntityOption> collectCompanies(Collection<EntityOption> companies, Collection<Long> companyIds) {
        return stream(companies)
                .filter(company ->
                        stream(companyIds).anyMatch(ids -> ids.equals(company.getId())))
                .collect(Collectors.toSet());
    }

    private Set<ProductShortView> collectProducts(Collection<ProductShortView> products, Collection<Long> productIds) {
        return stream(products)
                .filter(product ->
                        stream(productIds).anyMatch(ids -> ids.equals(product.getId())))
                .collect(Collectors.toSet());
    }

    private Set<PersonShortView> collectPersons(List<PersonShortView> personShortViews, Collection<Long> personIds) {
        return stream(personShortViews)
                .filter(personShortView ->
                        stream(personIds).anyMatch(ids -> ids.equals(personShortView.getId())))
                .collect(Collectors.toSet());
    }

    private Set<EntityOption> collectRegions(Collection<EntityOption> regions, Collection<Long> regionIds) {
        return stream(regions)
                .filter(region ->
                        stream(regionIds).anyMatch(id -> id.equals(region.getId())))
                .collect(Collectors.toSet());
    }

    private Set<ProductDirectionInfo> collectDirections(Collection<ProductDirectionInfo> directions,
                                                        Collection<Long> directionIds) {
        return stream(directions)
                .filter(direction ->
                        stream(directionIds).anyMatch(id -> id.equals(direction.getId())))
                .collect(Collectors.toSet());
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        regions.ensureDebugId(DebugIds.PROJECT_FILTER.REGION_SELECTOR);
        headManagers.ensureDebugId(DebugIds.PROJECT_FILTER.HEAD_MANAGER_SELECTOR);
        caseMembers.ensureDebugId(DebugIds.PROJECT_FILTER.TEAM_SELECTOR);
        initiatorCompanies.ensureDebugId(DebugIds.PROJECT_FILTER.COMPANY_SELECTOR);
        products.setAddEnsureDebugId(DebugIds.PROJECT_FILTER.PRODUCT_SELECTOR);
        commentCreationRange.setEnsureDebugId(DebugIds.PROJECT_FILTER.COMMENT_DATE_RANGE);
        sortField.setEnsureDebugId(DebugIds.PROJECT_FILTER.SORT_FIELD_SELECTOR);
    }

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    CleanableSearchBox search;

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField( provided = true )
    ProjectStateBtnGroupMulti states;

    @Inject
    @UiField(provided = true)
    RegionMultiSelector regions;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector headManagers;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector caseMembers;

    @Inject
    @UiField( provided = true )
    ProductDirectionMultiSelector direction;

    @Inject
    @UiField( provided = true )
    CompanyMultiSelector initiatorCompanies;

    @Inject
    @UiField( provided = true )
    ProductMultiSelector products;

    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker commentCreationRange;

    @UiField
    HTMLPanel onlyMineProjectsContainer;
    @UiField
    CheckBox onlyMineProjects;

    @Inject
    PolicyService policyService;

    private Consumer<Boolean> validateCallback;
    private Runnable filterChangeCallback;

    private Timer changeTimer = new Timer() {
        @Override
        public void run() {
            onFilterChanged();
        }
    };

    interface ProjectFilterParamWidgetUiBinder extends UiBinder<HTMLPanel, ProjectFilterParamWidget> {}
    private static ProjectFilterParamWidgetUiBinder ourUiBinder = GWT.create(ProjectFilterParamWidgetUiBinder.class);
}
