package ru.protei.portal.ui.document.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.decimalnumber.single.SingleDecimalNumberInput;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectButtonSelector;
import ru.protei.portal.ui.common.client.widget.stringselect.input.StringSelectInput;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditActivity;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditView;
import ru.protei.portal.ui.document.client.widget.doccategory.DocumentCategorySelector;
import ru.protei.portal.ui.document.client.widget.doctype.DocumentTypeSelector;
import ru.protei.portal.ui.document.client.widget.uploader.DocumentUploader;

import java.util.List;

public class DocumentEditView extends Composite implements AbstractDocumentEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        fileName.getElement().setAttribute("placeholder", lang.documentUploadPlaceholder());
        documentUploader.addChangeHandler(event ->
                fileName.setValue(documentUploader.getFilename()));
    }

    @Override
    public void setActivity(AbstractDocumentEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isDecimalNumbersCorrect() {
        if (decimalNumber.isValid()) {
            return true;
        }
        decimalNumber.setValid(false);
        return false;
    }

    @Override
    public void setEnabledProject(boolean isEnabled) {
        project.setEnabled(isEnabled);
    }

    @Override
    public void setVisibleUploader(boolean isVisible) {
        selectFileContainer.setVisible(isVisible);
        nameContainer.getElement().setClassName("form-group " + (isVisible ? "col-xs-6" : "col-xs-9"));
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValidable nameValidator() {
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
    public HasValue<ProjectInfo> project() {
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

    @Override
    public HasValue<Long> inventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public HasValue<List<String>> keywords() {
        return keywords;
    }

    @Override
    public HasValue<DecimalNumber> decimalNumber() {
        return decimalNumber;
    }

    @Override
    public DocumentUploader documentUploader() {
        return documentUploader;
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

    @UiHandler("selectFileButton")
    public void onSelectFileClicked(ClickEvent event) {
        documentUploader.click();
    }

    @UiHandler("documentCategory")
    public void onCategoryChanged(ValueChangeEvent<En_DocumentCategory> event) {
        documentType.setEnabled(true);
        documentType.setCategoryFilter(event.getValue());
        if (!documentType.getValue().getDocumentCategory().equals(event.getValue())) {
            documentType.setValue(null);
        }
    }


    @UiField
    ValidableTextBox name;

    @UiField
    HTMLPanel nameContainer;

    @UiField
    TextBox fileName;

    @Inject
    @UiField(provided = true)
    DocumentUploader documentUploader;

    @Inject
    @UiField(provided = true)
    DocumentTypeSelector documentType;

    @Inject
    @UiField(provided = true)
    DocumentCategorySelector documentCategory;

    @UiField
    TextArea annotation;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    @UiField
    LongBox inventoryNumber;

    @Inject
    @UiField(provided = true)
    ProjectButtonSelector project;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;

    @UiField
    TextBox created;

    @Inject
    @UiField(provided = true)
    StringSelectInput keywords;

    @Inject
    @UiField(provided = true)
    SingleDecimalNumberInput decimalNumber;

    @UiField
    Button selectFileButton;

    @UiField
    HTMLPanel selectFileContainer;

    @Inject
    Lang lang;

    AbstractDocumentEditActivity activity;

    private static DocumentViewUiBinder ourUiBinder = GWT.create(DocumentViewUiBinder.class);

    interface DocumentViewUiBinder extends UiBinder<HTMLPanel, DocumentEditView> {
    }
}
