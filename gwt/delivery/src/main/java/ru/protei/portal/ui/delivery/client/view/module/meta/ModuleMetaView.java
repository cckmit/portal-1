package ru.protei.portal.ui.delivery.client.view.module.meta;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.ui.common.client.widget.selector.module.ModuleStateFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.module.meta.AbstractModuleMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.module.meta.AbstractModuleMetaView;

import java.util.Date;

public class ModuleMetaView extends Composite implements AbstractModuleMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractModuleMetaActivity activity) {
        this.activity = activity;
        state.addValueChangeHandler(event -> activity.onStateChange());
        hwManager.addValueChangeHandler(event -> activity.onHwManagerChange());
        qcManager.addValueChangeHandler(event -> activity.onQcManagerChange());
    }

    @Override
    public HasValue<CaseState> state() {
        return state;
    }

    @Override
    public void setStateEnabled(boolean isEnabled) {
        state.setEnabled(isEnabled);
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
