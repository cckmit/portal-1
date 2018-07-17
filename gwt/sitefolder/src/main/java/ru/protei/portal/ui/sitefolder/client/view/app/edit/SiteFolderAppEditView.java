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
import ru.protei.portal.ui.sitefolder.client.activity.app.edit.AbstractSiteFolderAppEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.edit.AbstractSiteFolderAppEditView;
import ru.protei.portal.ui.sitefolder.client.view.server.widget.selector.ServerButtonSelector;

public class SiteFolderAppEditView extends Composite implements AbstractSiteFolderAppEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractSiteFolderAppEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setPlatformId(Long platformId) {
        server.setPlatformId(platformId);
    }

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

    private AbstractSiteFolderAppEditActivity activity;

    interface SiteFolderAppEditViewUiBinder extends UiBinder<HTMLPanel, SiteFolderAppEditView> {}
    private static SiteFolderAppEditViewUiBinder ourUiBinder = GWT.create(SiteFolderAppEditViewUiBinder.class);
}
