package ru.protei.portal.ui.common.client.view.contractfilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.CalculationType;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.contractfilter.AbstractContractFilterActivity;
import ru.protei.portal.ui.common.client.activity.contractfilter.AbstractContractFilterView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.casetag.CaseTagMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.state.ContractStatesMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.type.ContractTypesMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.contractor.multicontractor.MultiContractorSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeCustomMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

import java.util.List;
import java.util.Set;

public class ContractFilterView extends Composite implements AbstractContractFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        sortField.setType(ModuleType.CONTRACT);
        dateSigningRange.fillSelector(En_DateIntervalType.defaultTypes());
        dateValidRange.fillSelector(En_DateIntervalType.defaultTypes());
    }

    @Override
    public void setActivity(AbstractContractFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        sortField.setValue(En_SortField.contract_creation_date);
        sortDir.setValue(false);
        contractors.setValue(null);
        curators.setValue(null);
        organizations.setValue(null);
        managers.setValue(null);
        direction.setValue(null);
        resetStates();
        types.setValue(null);
        tags.setValue(null);
        kind.setValue(true);
        dateSigningRange.setValue(null);
        dateValidRange.setValue(null);
        deliveryNumber.setValue(null);
    }

    private void resetStates() {
        if (activity != null) {
            activity.resetContractStates();
        }
    }

    @Override
    public void clearFooterStyle() {
        footer.removeClassName("card-footer");
    }

    @Override
    public HasValue<String> searchString() {
        return name;
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
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
    }

    @Override
    public HasValue<Set<Contractor>> contractors() {
        return contractors;
    }

    @Override
    public HasValue<Set<EntityOption>> organizations() {
        return organizations;
    }

    @Override
    public HasValue<Set<En_ContractType>> types() {
        return types;
    }

    @Override
    public HasValue<Set<CaseTag>> tags() {
        return tags;
    }

    @Override
    public HasValue<Set<CaseState>> states() {
        return states;
    }

    @Override
    public HasValue<ProductDirectionInfo> direction() {
        return direction;
    }

    @Override
    public TakesValue<En_ContractKind> kind() {
        return new TakesValue<En_ContractKind>() {
            public void setValue(En_ContractKind value) {
                kind.setValue(value != null
                        ? value == En_ContractKind.RECEIPT
                        : null);
            }
            public En_ContractKind getValue() {
                Boolean value = kind.getValue();
                return value == null ? null : value
                        ? En_ContractKind.RECEIPT
                        : En_ContractKind.EXPENDITURE;
            }
        };
    }

    @Override
    public HasValue<DateIntervalWithType> dateSigningRange() {
        return dateSigningRange;
    }

    @Override
    public HasValue<DateIntervalWithType> dateValidRange() {
        return dateValidRange;
    }

    @Override
    public HasValue<Set<PersonShortView>> curators() {
        return curators;
    }

    @Override
    public HasValue<String> deliveryNumber() {
        return deliveryNumber;
    }

    @Override
    public void initCuratorsSelector(List<String> contractCuratorsDepartmentsIds) {
        EmployeeQuery query = new EmployeeQuery(null, false, true, En_SortField.person_full_name, En_SortDir.ASC);
        query.setDepartmentIds(contractCuratorsDepartmentsIds);
        curators.setEmployeeQuery(query);
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        if (activity != null) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "name" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        restartChangeTimer();
    }

    @UiHandler( "deliveryNumber" )
    public void onDeliveryNumberChanged( KeyUpEvent event ) {
        restartChangeTimer();
    }

    @UiHandler("sortDir")
    public void onSortDirChanged(ValueChangeEvent<Boolean> event) {
        restartChangeTimer();
    }

    @UiHandler("sortField")
    public void onSortFieldChanged(ValueChangeEvent<En_SortField> event) {
        restartChangeTimer();
    }

    @UiHandler({"managers", "curators"})
    public void onManagersOrCuratorsChanged(ValueChangeEvent<Set<PersonShortView>> event) {
        restartChangeTimer();
    }

    @UiHandler("contractors")
    public void onContractorsChanged(ValueChangeEvent<Set<Contractor>> event) {
        restartChangeTimer();
    }

    @UiHandler("organizations")
    public void onOrganizationsChanged(ValueChangeEvent<Set<EntityOption>> event) {
        restartChangeTimer();
    }

    @UiHandler("states")
    public void onStateChanged(ValueChangeEvent<Set<CaseState>> event) {
        restartChangeTimer();
    }

    @UiHandler("types")
    public void onTypeChanged(ValueChangeEvent<Set<En_ContractType>> event) {
        restartChangeTimer();
    }

    @UiHandler("tags")
    public void onTagChanged(ValueChangeEvent<Set<CaseTag>> event) {
        restartChangeTimer();
    }

    @UiHandler("direction")
    public void onDirectionChanged(ValueChangeEvent<ProductDirectionInfo> event) {
        restartChangeTimer();
    }

    @UiHandler("kind")
    public void onKindChanged(ValueChangeEvent<Boolean> event) {
        restartChangeTimer();
    }

    @UiHandler("dateSigningRange")
    public void onDateSigningRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        restartChangeTimer();
    }

    @UiHandler("dateValidRange")
    public void onDateValidRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        restartChangeTimer();
    }

    private void restartChangeTimer() {
        changeTimer.cancel();
        changeTimer.schedule(300);
    }

    private final Timer changeTimer = new Timer() {
        @Override
        public void run() {
            if (activity != null)
                activity.onFilterChanged();
        }
    };

    @Inject
    @UiField
    Lang lang;

    @UiField
    CleanableSearchBox name;

    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector managers;
    @Inject
    @UiField(provided = true)
    MultiContractorSelector contractors;
    @Inject
    @UiField(provided = true)
    HomeCompanyMultiSelector organizations;
    @Inject
    @UiField(provided = true)
    ContractStatesMultiSelector states;
    @Inject
    @UiField(provided = true)
    CaseTagMultiSelector tags;
    @Inject
    @UiField(provided = true)
    ProductDirectionButtonSelector direction;
    @Inject
    @UiField(provided = true)
    ContractTypesMultiSelector types;
    @UiField
    ThreeStateButton kind;
    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker dateSigningRange;
    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker dateValidRange;
    @UiField
    DivElement footer;
    @Inject
    @UiField(provided = true)
    EmployeeCustomMultiSelector curators;
    @UiField
    TextBox deliveryNumber;

    private AbstractContractFilterActivity activity;

    private static FilterViewUiBinder outUiBinder = GWT.create(FilterViewUiBinder.class);

    interface FilterViewUiBinder extends UiBinder<HTMLPanel, ContractFilterView> {
    }
}
