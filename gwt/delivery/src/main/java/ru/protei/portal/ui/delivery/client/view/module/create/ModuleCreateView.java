package ru.protei.portal.ui.delivery.client.view.module.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.module.create.AbstractModuleCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.module.create.AbstractModuleCreateView;
import ru.protei.portal.ui.delivery.client.view.module.meta.ModuleMetaView;
import ru.protei.portal.ui.delivery.client.view.module.namedescription.ModuleNameDescriptionEditView;

import java.util.Date;

public class ModuleCreateView extends Composite implements AbstractModuleCreateView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractModuleCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasValue<String> serialNumber() {
        return serialNumber;
    }

    @Override
    public HasValue<String> name() {
        return nameDescription.name();
    }

    @Override
    public HasValue<String> description() {
        return nameDescription.description();
    }

    @Override
    public HasValue<CaseState> state() {
        return meta.state();
    }

    @Override
    public void setManager(String value) {
        meta.setManager(value);
    }

    @Override
    public HasValue<PersonShortView> hwManager() {
        return meta.hwManager();
    }

    @Override
    public HasValue<PersonShortView> qcManager() {
        return meta.qcManager();
    }

    @Override
    public void setCustomerCompany(String value) {
        meta.setCustomerCompany(value);
    }

    @Override
    public HasValue<Date> departureDate() {
        return meta.departureDate();
    }

    @Override
    public HasValue<Date> buildDate() {
        return meta.buildDate();
    }

    @Override
    public void setAllowChangingState(boolean isAllow) {
        meta.setStateEnabled(isAllow);
    }

    @Override
    public void setBuildDateValid(boolean isValid) {
        meta.setBuildDateValid(isValid);
    }

    @Override
    public void setDepartureDateValid(boolean isValid) {
        meta.setDepartureDateValid(isValid);
    }

    @Override
    public boolean isBuildDateEmpty() {
        return meta.isBuildDateEmpty();
    }

    @Override
    public boolean isDepartureDateEmpty() {
        return meta.isDepartureDateEmpty();
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    // todo нужно будет добавить ModuleCommonMeta для разного поведения изменения полей в блоке meta
    // на страницах создания и редактирования модулей. Сделать как в Delivery
//    @UiHandler("departureDate")
//    public void onDepartureDateChanged(ValueChangeEvent<Date> event) {
//        if (commonActivity != null) {
//            commonActivity.onDepartureDateChanged();
//        }
//    }

    @UiField
    Lang lang;
    @UiField
    ValidableTextBox serialNumber;
    @Inject
    @UiField(provided = true)
    ModuleNameDescriptionEditView nameDescription;
    @Inject
    @UiField(provided = true)
    ModuleMetaView meta;
    @UiField
    Button saveButton;

    private AbstractModuleCreateActivity activity;

    private static ModuleCreateView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleCreateView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleCreateView> {}
}
