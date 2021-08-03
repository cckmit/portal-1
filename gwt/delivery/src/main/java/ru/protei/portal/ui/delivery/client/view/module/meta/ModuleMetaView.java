package ru.protei.portal.ui.delivery.client.view.module.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.module.ModuleStateFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.module.meta.AbstractModuleMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.module.meta.AbstractModuleMetaView;

public class ModuleMetaView extends Composite implements AbstractModuleMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
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

    private AbstractModuleMetaActivity activity;

    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleMetaView> {}
    private static ModuleViewUiBinder ourUiBinder = GWT.create(ModuleViewUiBinder.class);
}
