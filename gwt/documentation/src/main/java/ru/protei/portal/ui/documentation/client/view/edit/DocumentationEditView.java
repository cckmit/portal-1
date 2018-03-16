package ru.protei.portal.ui.documentation.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.documentation.client.activity.edit.AbstractDocumentationEditActivity;
import ru.protei.portal.ui.documentation.client.activity.edit.AbstractDocumentationEditView;
import ru.protei.portal.ui.documentation.client.widget.doctype.DocumentTypeSelector;

public class DocumentationEditView extends Composite implements AbstractDocumentationEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        created.setEnabled(false);
    }

    @Override
    public void setActivity(AbstractDocumentationEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<DocumentType> documentType() {
        return documentType;
    }

    @Override
    public HasValue<String> annotation() {
        return annotation;
    }

    @Override
    public HasValue<String> project() {
        return project;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<String> created() {
        return created;
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
    ValidableTextBox name;

    @Inject
    @UiField(provided = true)
    DocumentTypeSelector documentType;

    @UiField
    TextArea annotation;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    @UiField
    TextBox project;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;

    @UiField
    TextBox created;

    AbstractDocumentationEditActivity activity;

    private static DocumentationViewUiBinder ourUiBinder = GWT.create(DocumentationViewUiBinder.class);

    interface DocumentationViewUiBinder extends UiBinder<HTMLPanel, DocumentationEditView> {
    }
}
