package ru.protei.portal.ui.delivery.client.view.module.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.module.create.AbstractModuleCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.module.create.AbstractModuleCreateView;
import ru.protei.portal.ui.delivery.client.view.module.namedescription.ModuleNameDescriptionEditView;


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
    public HasWidgets getMetaViewContainer() {
        return metaViewContainer;
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
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

    private AbstractModuleCreateActivity activity;

    private static ModuleCreateView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleCreateView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleCreateView> {}
}
