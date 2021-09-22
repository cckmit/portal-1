package ru.protei.portal.ui.delivery.client.view.delivery.module.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.create.AbstractModuleCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.create.AbstractModuleCreateView;
import ru.protei.portal.ui.delivery.client.view.delivery.module.namedescription.ModuleNameDescriptionEditView;


public class ModuleCreateView extends Composite implements AbstractModuleCreateView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
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
    public HasWidgets getMetaViewContainer() {
        return metaViewContainer;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        saveButton.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.CANCEL_BUTTON);
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }

    }

    @UiField
    Lang lang;
    @UiField
    ValidableTextBox serialNumber;
    @Inject
    @UiField(provided = true)
    ModuleNameDescriptionEditView nameDescription;
    @UiField
    HTMLPanel metaViewContainer;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    private AbstractModuleCreateActivity activity;

    private static ModuleCreateView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleCreateView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleCreateView> {}
}
