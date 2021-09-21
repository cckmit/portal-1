package ru.protei.portal.ui.delivery.client.view.delivery.module.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.module.ModuleStateFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.meta.AbstractModuleMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.meta.AbstractModuleMetaView;

import java.util.Date;

public class ModuleMetaView extends Composite implements AbstractModuleMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractModuleMetaActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<CaseState> state() {
        return state;
    }

    @Override
    public void setManager(String value) {
        manager.setValue(value);
    }

    @Override
    public void setCustomerCompany(String value) {
        customerCompany.setValue(value);
    }

    @Override
    public HasValue<PersonShortView> hwManager() {
        return hwManager;
    }

    @Override
    public HasValue<PersonShortView> qcManager() {
        return qcManager;
    }

    @Override
    public HasValue<Date> buildDate() {
        return buildDate;
    }

    @Override
    public HasValue<Date> departureDate() {
        return departureDate;
    }

    @Override
    public void setBuildDateValid(boolean isValid) {
        buildDate.markInputValid(isValid);
    }

    @Override
    public void setDepartureDateValid(boolean isValid) {
        departureDate.markInputValid(isValid);
    }

    @Override
    public boolean isBuildDateEmpty() {
        return HelperFunc.isEmpty(buildDate.getInputValue());
    }

    @Override
    public boolean isDepartureDateEmpty() {
        return HelperFunc.isEmpty(departureDate.getInputValue());
    }

    @Override
    public void setAllowChangingState(boolean isAllow) {
        state.setEnabled(isAllow);
    }

    @Override
    public HasEnabled stateEnabled() {
        return state;
    }

    @Override
    public HasEnabled hwManagerEnabled() {
        return hwManager;
    }

    @Override
    public HasEnabled qcManagerEnabled() {
        return qcManager;
    }

    @Override
    public HasEnabled buildDateEnabled() {
        return buildDate;
    }

    @Override
    public HasEnabled departureDateEnabled() {
        return departureDate;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        state.setEnsureDebugId(DebugIds.DELIVERY.KIT.MODULE.STATE);
        customerCompany.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.CUSTOMER_COMPANY);
        manager.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.MANAGER);
        hwManager.ensureLabelDebugId(DebugIds.DELIVERY.KIT.MODULE.HW_MANAGER);
        qcManager.ensureLabelDebugId(DebugIds.DELIVERY.KIT.MODULE.QC_MANAGER);
        buildDate.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.BUILD_DATE);
        departureDate.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.DEPARTURE_DATE);
    }

    @UiHandler("state")
    public void onStateChanged(ValueChangeEvent<CaseState> event) {
        activity.onStateChanged();
    }

    @UiHandler("hwManager")
    public void onHwManagerChanged(ValueChangeEvent<PersonShortView> event) {
        activity.onHwManagerChanged();
    }

    @UiHandler("qcManager")
    public void onQcManagerChanged(ValueChangeEvent<PersonShortView> event) {
        activity.onQcManagerChanged();
    }

    @UiHandler("buildDate")
    public void onBuildDateChanged(ValueChangeEvent<Date> event) {
        activity.onBuildDateChanged();
    }

    @UiHandler("departureDate")
    public void onDepartureDateChanged(ValueChangeEvent<Date> event) {
        activity.onDepartureDateChanged();
    }

    @Inject
    @UiField( provided = true )
    ModuleStateFormSelector state;
    @UiField
    ValidableTextBox manager;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector hwManager;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector qcManager;
    @UiField
    ValidableTextBox customerCompany;
    @Inject
    @UiField(provided = true)
    SinglePicker buildDate;
    @Inject
    @UiField(provided = true)
    SinglePicker departureDate;

    private AbstractModuleMetaActivity activity;

    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleMetaView> {}
    private static ModuleViewUiBinder ourUiBinder = GWT.create(ModuleViewUiBinder.class);
}
