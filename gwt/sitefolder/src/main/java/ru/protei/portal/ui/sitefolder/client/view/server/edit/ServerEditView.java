package ru.protei.portal.ui.sitefolder.client.view.server.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.sitefolder.client.activity.server.edit.AbstractServerEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.edit.AbstractServerEditView;
import ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector.PlatformButtonSelector;

public class ServerEditView extends Composite implements AbstractServerEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractServerEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCompanyId(Long companyId) {}

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<EntityOption> platform() {
        return platform;
    }

    @Override
    public HasValue<String> ip() {
        return ip;
    }

    @Override
    public HasValue<String> parameters() {
        return parameters;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasWidgets listContainer() {
        return listContainer;
    }

    @Override
    public HasVisibility listContainerVisibility() {
        return listContainer;
    }

    @Override
    public HasVisibility listContainerHeaderVisibility() {
        return listContainerHeader;
    }

    @Override
    public HasEnabled platformEnabled() {
        return platform;
    }

    @Override
    public HasValidable nameValidator() {
        return name;
    }

    @Override
    public HasValidable platformValidator() {
        return platform;
    }

    @Override
    public HasVisibility createButtonVisibility() {
        return createButton;
    }

    @Override
    public HasVisibility openButtonVisibility() {
        return openButton;
    }

    @UiHandler("saveButton")
    public void saveButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void cancelButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiHandler("openButton")
    public void openButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOpenClicked();
        }
    }

    @UiHandler("createButton")
    public void createButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCreateClicked();
        }
    }

    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    PlatformButtonSelector platform;
    @UiField
    TextBox ip;
    @UiField
    TextArea parameters;
    @UiField
    TextArea comment;
    @UiField
    HTMLPanel listContainerHeader;
    @UiField
    HTMLPanel listContainer;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    Button createButton;
    @UiField
    Button openButton;

    private AbstractServerEditActivity activity;

    interface SiteFolderServerEditViewUiBinder extends UiBinder<HTMLPanel, ServerEditView> {}
    private static SiteFolderServerEditViewUiBinder ourUiBinder = GWT.create(SiteFolderServerEditViewUiBinder.class);
}
