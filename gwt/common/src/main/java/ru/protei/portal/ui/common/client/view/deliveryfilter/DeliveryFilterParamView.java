package ru.protei.portal.ui.common.client.view.deliveryfilter;

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
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryFilterModel;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryFilterParamView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.deliverystate.DeliveryStatesOptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.util.AlternativeKeyboardLayoutTextService.makeAlternativeSearchString;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.fromDateRange;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

public class DeliveryFilterParamView extends Composite implements AbstractDeliveryFilterParamView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortDir.setValue(false);
        fillDateRanges(dateDepartureRange);
        products.setTypes(En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
    }

    @Override
    public void setModel(AbstractDeliveryFilterModel model) {
        this.model = model;
    }

    @Override
    public void setInitiatorCompaniesModel(AsyncSelectorModel companyModel) {
        companies.setAsyncModel(companyModel);
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public void setCreatedRangeMandatory(boolean isMandatory) {
        dateDepartureRange.setTypeMandatory(isMandatory);
    }

    @Override
    public void setDepartureRangeValid(boolean isTypeValid, boolean isRangeValid) {
        dateDepartureRange.setValid(isTypeValid, isRangeValid);
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
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
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
    public HasVisibility managersVisibility() {
        return managers;
    }

    @Override
    public void resetFilter(DateIntervalWithType dateModified) {
        companies.setValue(null);
        managers.setValue(null);
        products.setValue(null);
        state.setValue(null);
        dateDepartureRange.setValue(null);
        sortField.setValue(En_SortField.delivery_departure_date);
        sortDir.setValue(false);
        search.setValue("");

        if (isAttached()) {
            onFilterChanged();
        }
    }

    @Override
    public void fillFilterFields(DeliveryQuery deliveryQuery, SelectorsParams filter) {
        search.setValue(deliveryQuery.getSearchString());
        sortDir.setValue(deliveryQuery.getSortDir() == null ? null : deliveryQuery.getSortDir().equals(En_SortDir.ASC));
        sortField.setValue(deliveryQuery.getSortField() == null ? En_SortField.delivery_departure_date : deliveryQuery.getSortField());
        dateDepartureRange.setValue(fromDateRange(deliveryQuery.getDepartureDateRange()));
        state.setValue(toSet(deliveryQuery.getStateIds(), id -> new CaseState(id)));

        Set<EntityOption> initiatorsCompanies = applyCompanies( filter.getCompanyEntityOptions(), deliveryQuery.getCompanyIds() );
        companies.setValue(initiatorsCompanies);

        Set<PersonShortView> personShortViews = new LinkedHashSet<>();
        if (emptyIfNull(deliveryQuery.getManagerIds()).contains(CrmConstants.Employee.UNDEFINED)) {
            personShortViews.add(new PersonShortView(lang.employeeWithoutManager(), CrmConstants.Employee.UNDEFINED));
        }
        personShortViews.addAll(applyPersons(filter.getPersonShortViews(), deliveryQuery.getManagerIds()));
        managers.setValue(personShortViews);

        Set<ProductShortView> productsShortView = new LinkedHashSet<>();
        if (emptyIfNull(deliveryQuery.getProductIds()).contains(CrmConstants.Product.UNDEFINED)) {
            productsShortView.add(new ProductShortView(CrmConstants.Product.UNDEFINED, lang.productWithout(), 0));
        }
        productsShortView.addAll(emptyIfNull(filter.getProductShortViews()));
        products.setValue(productsShortView);

        onFilterChanged();
    }

    @Override
    public DeliveryQuery getFilterFields(En_DeliveryFilterType filterType) {
        DeliveryQuery query = new DeliveryQuery();

        switch (filterType) {
            case DELIVERY_OBJECTS: {
                String searchString = search.getValue();
                query.setSearchString(isBlank(searchString) ? null : searchString);
                query.setAlternativeSearchString( makeAlternativeSearchString( searchString));
                query.setSortField(sortField.getValue());
                query.setSortDir(sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
                query.setCompanyIds(getCompaniesIdList(companies.getValue()));
                query.setProductIds(getProductsIdList(products.getValue()));
                query.setManagerIds(getManagersIdList(managers.getValue()));
                query.setDepartureDateRange(toDateRange(dateDepartureRange.getValue()));
                query.setStateIds(nullIfEmpty(toList(states().getValue(), CaseState::getId)));
                break;
            }
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
    public void resetRanges() {
        dateDepartureRange.setValue(null);
    }

    private void fillDateRanges (TypedSelectorRangePicker rangePicker) {
        rangePicker.fillSelector(En_DateIntervalType.issueTypes());
    }

    @UiHandler("search")
    public void onSearchChanged(ValueChangeEvent<String> event) {
        startFilterChangedTimer();
    }

    @UiHandler("dateDepartureRange")
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
        onFilterChanged();
    }

    @UiHandler("managers")
    public void onManagersSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onFilterChanged();
    }

    @UiHandler("state")
    public void onStateSelected(ValueChangeEvent<Set<CaseState>> event) {
        onFilterChanged();
    }

    public void applyVisibility(En_DeliveryFilterType filterType) {
        if (filterType == null) {
            return;
        }

        final boolean isCustomer = isCustomer();
        final boolean isSubcontractor = policyService.isSubcontractorCompany();

        search.setVisible(filterType.equals(En_DeliveryFilterType.DELIVERY_OBJECTS));
        managers.setVisible((!isCustomer || isSubcontractor) && filterType.equals(En_DeliveryFilterType.DELIVERY_OBJECTS));
    }

    private boolean isCustomer() {
        return !policyService.hasSystemScopeForPrivilege(En_Privilege.DELIVERY_VIEW);
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
        } else {
            setManagersErrorStyle(false);
        }
        return null;
    }

    public boolean isCreatedRangeTypeValid() {
        return !dateDepartureRange.isTypeMandatory()
               || (dateDepartureRange.getValue() != null
                   && dateDepartureRange.getValue().getIntervalType() != null);
    }

    public boolean isDepartureRangeValid() {
        return isDateRangeValid(dateDepartureRange.getValue());
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

    private Set<EntityOption> applyCompanies(List<EntityOption> companies, List<Long> companyIds) {
        return stream(companies)
                .filter(company ->
                        stream(companyIds).anyMatch(ids -> ids.equals(company.getId())))
                .collect(Collectors.toSet());
    }

    private void ensureDebugIds() {
        search.setDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        search.setDebugIdAction(DebugIds.FILTER.SEARCH_CLEAR_BUTTON);
        dateDepartureRange.setEnsureDebugId(DebugIds.FILTER.DATE_CREATED_RANGE_CONTAINER);
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
        labelSortBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.SORT_FIELD_LABEL);
        deliveryState.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.ISSUE_STATE_LABEL);
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

    @Inject
    @UiField
    Lang lang;
    @UiField
    CleanableSearchBox search;
    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker dateDepartureRange;
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
    EmployeeMultiSelector managers;
    @UiField
    LabelElement labelSortBy;
    @UiField
    LabelElement deliveryState;
    @Inject
    @UiField(provided = true)
    DeliveryStatesOptionList state;
    @UiField
    DivElement sortByContainer;
    @UiField
    DivElement stateContainer;
    @Inject
    PolicyService policyService;

    private Timer timer = null;
    private AbstractDeliveryFilterModel model;

    interface DeliveryFilterUiBinder extends UiBinder<HTMLPanel, DeliveryFilterParamView> {}
    private static DeliveryFilterUiBinder ourUiBinder = GWT.create(DeliveryFilterUiBinder.class);
}
