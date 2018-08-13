package ru.protei.portal.ui.sitefolder.client.view.app.edit;

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
import ru.protei.portal.ui.sitefolder.client.activity.app.edit.AbstractApplicationEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.edit.AbstractApplicationEditView;
import ru.protei.portal.ui.sitefolder.client.view.server.widget.selector.ServerButtonSelector;

public class ApplicationEditView extends Composite implements AbstractApplicationEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractApplicationEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setPlatformId(Long platformId) {}

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<EntityOption> server() {
        return server;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasValidable nameValidator() {
        return name;
    }

    @Override
    public HasWidgets pathsContainer() {
        return pathsContainer;
    }

    @Override
    public HasEnabled serverEnabled() {
        return server;
    }

    @Override
    public HasValidable serverValidator() {
        return server;
    }

    @Override
    public HasVisibility cloneButtonVisibility() {
        return cloneButton;
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

    @UiHandler("cloneButton")
    public void cloneButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCloneClicked();
        }
    }

    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    ServerButtonSelector server;
    @UiField
    TextArea comment;
    @UiField
    HTMLPanel pathsContainer;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    Button cloneButton;

    private AbstractApplicationEditActivity activity;

    interface SiteFolderAppEditViewUiBinder extends UiBinder<HTMLPanel, ApplicationEditView> {}
    private static SiteFolderAppEditViewUiBinder ourUiBinder = GWT.create(SiteFolderAppEditViewUiBinder.class);
}
