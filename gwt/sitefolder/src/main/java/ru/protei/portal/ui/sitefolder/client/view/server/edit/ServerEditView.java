package ru.protei.portal.ui.sitefolder.client.view.server.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.sitefolder.client.activity.server.edit.AbstractServerEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.edit.AbstractServerEditView;
import ru.protei.portal.ui.common.client.widget.selector.platform.PlatformButtonSelector;
import ru.protei.portal.ui.sitefolder.client.widget.selector.servergroup.ServerGroupButtonSelector;
import ru.protei.portal.ui.sitefolder.client.widget.selector.servergroup.ServerGroupModel;

public class ServerEditView extends Composite implements AbstractServerEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));

        serverGroup.setEditHandler(serverGroup -> activity.onEditServerGroupClicked(serverGroup));
        serverGroup.addAddHandler(event -> activity.onCreateServerGroupClicked());
    }

    @Override
    public void setActivity(AbstractServerEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setServerGroupModel(ServerGroupModel serverGroupModel) {
        serverGroup.setAsyncModel(serverGroupModel);
    }

    @Override
    public void setCompanyId(Long companyId) {}

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<PlatformOption> platform() {
        return platform;
    }

    @Override
    public HasValue<ServerGroup> serverGroup() {
        return serverGroup;
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
    public HasEnabled serverGroupEnabled() {
        return serverGroup;
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

    @UiHandler("platform")
    public void onPlatformChanged(ValueChangeEvent<PlatformOption> event) {
        if (activity != null) {
            activity.onPlatformChanged();
        }
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
    @Inject
    @UiField(provided = true)
    ServerGroupButtonSelector serverGroup;
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
    @Inject
    @UiField
    Lang lang;

    private AbstractServerEditActivity activity;

    interface SiteFolderServerEditViewUiBinder extends UiBinder<HTMLPanel, ServerEditView> {}
    private static SiteFolderServerEditViewUiBinder ourUiBinder = GWT.create(SiteFolderServerEditViewUiBinder.class);
}
