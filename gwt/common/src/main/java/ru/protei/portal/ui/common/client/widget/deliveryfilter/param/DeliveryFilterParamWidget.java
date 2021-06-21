package ru.protei.portal.ui.common.client.widget.deliveryfilter.param;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.DeliveryFilterUtils;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.deliverystate.DeliveryStatesOptionList;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterParamView;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.util.AlternativeKeyboardLayoutTextService.makeAlternativeSearchString;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.searchCaseNumber;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.fromDateRange;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

public class DeliveryFilterParamWidget extends Composite implements FilterParamView<DeliveryQuery> {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        search.getElement().setPropertyString("placeholder", lang.search());
        sortField.setType( ModuleType.DELIVERY );
        fillDateRanges(dateDepartureRange);
        products.setTypes(En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
    }

    @Override
    public void resetFilter() {
        companies.setValue(null);
        managers.setValue(null);
        products.setValue(null);
        states.setValue(null);
        dateDepartureRange.setValue(null);
        sortField.setValue(En_SortField.delivery_creation_date);
        sortDir.setValue(false);
        search.setValue("");
        military.setValue(null);

        if (isAttached()) {
            onFilterChanged();
        }
    }

    @Override
    public DeliveryQuery getQuery() {
        DeliveryQuery query = new DeliveryQuery();
        String searchString = search.getValue();
        query.setSerialNumbers(DeliveryFilterUtils.searchSerialNumber(searchString));
        if (CollectionUtils.isNotEmpty(query.getSerialNumbers())) {
            return query;
        }
        query.setSearchString(isBlank(searchString) ? null : searchString);
        query.setAlternativeSearchString( makeAlternativeSearchString( searchString));
        query.setSortField(sortField.getValue());
        query.setSortDir(sortDir.getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setCompanyIds(getCompaniesIdList(companies.getValue()));
        query.setProductIds(getProductsIdList(products.getValue()));
        query.setManagerIds(getManagersIdList(managers.getValue()));
        query.setDepartureDateRange(toDateRange(dateDepartureRange.getValue()));
        query.setStateIds(nullIfEmpty(toList(states.getValue(), CaseState::getId)));
        query.setDeleted(CaseObject.NOT_DELETED);
        query.setMilitary(military.getValue());

        return query;
    }

    @Override
    public void fillFilterFields(DeliveryQuery deliveryQuery, SelectorsParams filter) {
        search.setValue(deliveryQuery.getSearchString());
        sortDir.setValue(deliveryQuery.getSortDir() == null ? null : deliveryQuery.getSortDir().equals(En_SortDir.ASC));
        sortField.setValue(deliveryQuery.getSortField() == null ? En_SortField.delivery_creation_date : deliveryQuery.getSortField());
        dateDepartureRange.setValue(fromDateRange(deliveryQuery.getDepartureDateRange()));
        states.setValue(toSet(deliveryQuery.getStateIds(), id -> new CaseState(id)));

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

        military.setValue(deliveryQuery.getMilitary());

        if (validate()) {
            onFilterChanged();
        }
    }

    @Override
    public void setValidateCallback(Consumer<Boolean> callback) {
        this.validateCallback = callback;
    }

    @Override
    public void setOnFilterChangeCallback(Runnable callback) {
        this.filterChangeCallback = callback;
    }

    public void setStateFilter(Selector.SelectorFilter<CaseState> caseStateFilter) {
        states.setFilter(caseStateFilter);
    }

    @UiHandler("search")
    public void onSearchChanged(ValueChangeEvent<String> event) {
        startFilterChangedTimer();
    }

    @UiHandler("dateDepartureRange")
    public void onDateRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        if (validate()) {
            onFilterChanged();
        }
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

    @UiHandler("states")
    public void onStateSelected(ValueChangeEvent<Set<CaseState>> event) {
        onFilterChanged();
    }

    @UiHandler("military")
    public void onMilitaryChanged(ValueChangeEvent<Boolean> event) {
        onFilterChanged();
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

    private void fillDateRanges (TypedSelectorRangePicker rangePicker) {
        rangePicker.fillSelector(En_DateIntervalType.issueTypes());
    }

    private void ensureDebugIds() {
        search.setDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        search.setDebugIdAction(DebugIds.FILTER.SEARCH_CLEAR_BUTTON);
        dateDepartureRange.setEnsureDebugId(DebugIds.FILTER.DATE_DEPARURE_RANGE_CONTAINER);
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
        deliveryState.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DELIVERY.FILTER.STATE_LABEL);
        military.setYesEnsureDebugId(DebugIds.DELIVERY.FILTER.MILITARY_YES);
        military.setNotDefinedEnsureDebugId(DebugIds.DELIVERY.FILTER.MILITARY_ANY);
        military.setNoEnsureDebugId(DebugIds.DELIVERY.FILTER.MILITARY_NO);
    }


    private boolean validate() {
        boolean isValid = isDateRangeValid(dateDepartureRange.getValue());

        if (validateCallback != null) {
            validateCallback.accept(isValid);
        }

        return isValid;
    }

    public boolean isDateRangeValid(DateIntervalWithType dateRange) {
        if (dateRange == null || dateRange.getIntervalType() == null) {
            return true;
        }

        return !Objects.equals(dateRange.getIntervalType(), En_DateIntervalType.FIXED) || dateRange.getInterval().isValid();
    }

    private void onFilterChanged() {
        if (filterChangeCallback != null) {
            filterChangeCallback.run();
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
    DeliveryStatesOptionList states;
    @UiField
    DivElement sortByContainer;
    @UiField
    DivElement stateContainer;
    @UiField
    ThreeStateButton military;

    private Timer timer = null;
    private Consumer<Boolean> validateCallback;
    private Runnable filterChangeCallback;

    interface ProjectFilterParamWidgetUiBinder extends UiBinder<HTMLPanel, DeliveryFilterParamWidget> {}
    private static ProjectFilterParamWidgetUiBinder ourUiBinder = GWT.create(ProjectFilterParamWidgetUiBinder.class);
}
