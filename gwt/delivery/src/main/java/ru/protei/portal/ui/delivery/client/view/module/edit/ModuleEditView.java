package ru.protei.portal.ui.delivery.client.view.module.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.module.edit.AbstractModuleEditActivity;
import ru.protei.portal.ui.delivery.client.activity.module.edit.AbstractModuleEditView;

public class ModuleEditView extends Composite implements AbstractModuleEditView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractModuleEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getNameContainer() {
        return nameContainer;
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaContainer;
    }

    @Override
    public HasVisibility showEditViewButtonVisibility() {
        return showEditViewButton;
    }

    @Override
    public HasVisibility nameAndDescriptionEditButtonVisibility() {
        return nameAndDescriptionEditButton;
    }

    @Override
    public void setCreatedBy(String value) {
        createdBy.setInnerHTML( value );
    }

    @Override
    public void setModuleNumber( String serialNumber ) {
        this.serialNumber.setInnerText(serialNumber);
    }

    @UiHandler("nameAndDescriptionEditButton")
    public void onNameAndDescriptionEditButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onNameAndDescriptionEditClicked();
        }
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root; // для изменения стилей при переключении проевью/полный экран
    @UiField
    Anchor showEditViewButton;
    @UiField
    Anchor nameAndDescriptionEditButton;

    @UiField
    SpanElement serialNumber;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    Element createdBy;
    @UiField
    HTMLPanel metaContainer;

    private AbstractModuleEditActivity activity;

    private static ModuleEditView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleEditView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleEditView> {}
}
