package ru.protei.portal.ui.documentation.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.documentation.client.activity.edit.AbstractDocumentationEditActivity;
import ru.protei.portal.ui.documentation.client.activity.edit.AbstractDocumentationEditView;

public class DocumentationEditView extends Composite implements AbstractDocumentationEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractDocumentationEditActivity activity) {
        this.activity = activity;
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

    @Override
    public void setVisibilitySettingsForCreated(boolean isVisible) {

    }

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    AbstractDocumentationEditActivity activity;

    private static DocumentationViewUiBinder ourUiBinder = GWT.create(DocumentationViewUiBinder.class);

    interface DocumentationViewUiBinder extends UiBinder<HTMLPanel, DocumentationEditView> {
    }
}
