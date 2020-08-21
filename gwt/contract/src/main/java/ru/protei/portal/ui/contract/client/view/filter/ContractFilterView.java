package ru.protei.portal.ui.contract.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.contractor.multicontractor.MultiContractorSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.contract.client.activity.filter.AbstractContractFilterActivity;
import ru.protei.portal.ui.contract.client.activity.filter.AbstractContractFilterView;
import ru.protei.portal.ui.contract.client.widget.selector.multi.ContractStateMultiSelector;
import ru.protei.portal.ui.contract.client.widget.selector.multi.ContractTypeMultiSelector;

import java.util.Set;

public class ContractFilterView extends Composite implements AbstractContractFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        sortField.setType(ModuleType.CONTRACT);
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
        organizations.setValue(null);
        managers.setValue(null);
        direction.setValue(null);
        states.setValue(null);
        types.setValue(null);
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
    public HasValue<Set<En_ContractState>> states() {
        return states;
    }

    @Override
    public HasValue<ProductDirectionInfo> direction() {
        return direction;
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

    @UiHandler("sortDir")
    public void onSortDirChanged(ValueChangeEvent<Boolean> event) {
        restartChangeTimer();
    }

    @UiHandler("sortField")
    public void onSortFieldChanged(ValueChangeEvent<En_SortField> event) {
        restartChangeTimer();
    }

    @UiHandler("managers")
    public void onManagersChanged(ValueChangeEvent<Set<PersonShortView>> event) {
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
    public void onStateChanged(ValueChangeEvent<Set<En_ContractState>> event) {
        restartChangeTimer();
    }

    @UiHandler("types")
    public void onTypeChanged(ValueChangeEvent<Set<En_ContractType>> event) {
        restartChangeTimer();
    }

    @UiHandler("direction")
    public void onDirectionChanged(ValueChangeEvent<ProductDirectionInfo> event) {
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
    ContractStateMultiSelector states;
    @Inject
    @UiField(provided = true)
    ProductDirectionButtonSelector direction;
    @Inject
    @UiField(provided = true)
    ContractTypeMultiSelector types;

    private AbstractContractFilterActivity activity;

    private static FilterViewUiBinder outUiBinder = GWT.create(FilterViewUiBinder.class);

    interface FilterViewUiBinder extends UiBinder<HTMLPanel, ContractFilterView> {
    }
}
