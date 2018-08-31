package ru.protei.portal.ui.equipment.client.view.document.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.document.doccategory.DocumentCategorySelector;
import ru.protei.portal.ui.common.client.widget.document.doctype.DocumentTypeSelector;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;
import ru.protei.portal.ui.common.client.widget.document.uploader.DocumentUploader;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.stringselect.input.StringSelectInput;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.equipment.client.activity.document.edit.AbstractEquipmentDocumentEditActivity;
import ru.protei.portal.ui.equipment.client.activity.document.edit.AbstractEquipmentDocumentEditView;

import java.util.List;

public class EquipmentDocumentEditView extends Composite implements AbstractEquipmentDocumentEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        fileName.getElement().setAttribute("placeholder", lang.documentUploadPlaceholder());
    }

    @Override
    public void setActivity(AbstractEquipmentDocumentEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCreated(String date) {
        created.setInnerText(date);
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public AbstractDocumentUploader documentUploader() {
        return documentUploader;
    }

    @Override
    public HasValue<Boolean> approved() {
        return approved;
    }

    @Override
    public void setDocumentCategory(En_DocumentCategory documentCategory) {
        this.documentCategory.setValue(documentCategory);
        this.documentType.setCategoryFilter(documentCategory);
    }

    @Override
    public HasValue<DocumentType> documentType() {
        return documentType;
    }

    @Override
    public HasValue<String> version() {
        return version;
    }

    @Override
    public HasValue<String> decimalNumber() {
        return decimalNumber;
    }

    @Override
    public HasValue<Long> inventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public HasValue<PersonShortView> registrar() {
        return registrar;
    }

    @Override
    public HasValue<PersonShortView> contractor() {
        return contractor;
    }

    @Override
    public HasValue<String> annotation() {
        return annotation;
    }

    @Override
    public HasValue<List<String>> keywords() {
        return keywords;
    }

    @Override
    public void setApprovedMode(boolean off) {
        if (off) {
            nameContainer.setStyleName("form-group col-xs-6");
            documentUploaderContainer.setVisible(true);
        } else {
            nameContainer.setStyleName("form-group col-xs-9");
            documentUploaderContainer.setVisible(false);
        }
    }

    @Override
    public HasEnabled decimalNumberEnabled() {
        return decimalNumber;
    }

    @Override
    public HasEnabled documentCategoryEnabled() {
        return documentCategory;
    }

    @Override
    public HasEnabled approvedEnabled() {
        return approved;
    }

    @Override
    public HasEnabled saveButtonEnabled() {
        return saveButton;
    }

    @Override
    public HasEnabled cancelButtonEnabled() {
        return cancelButton;
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

    @UiHandler("documentUploader")
    public void onFilenameChanged(ChangeEvent event) {
        fileName.setValue(documentUploader.getFilename());
    }

    @UiHandler("approved")
    public void onApprovedChanged(ValueChangeEvent<Boolean> event) {
        if (activity != null) {
            activity.onApproveChanged(event.getValue());
        }
    }

    private AbstractEquipmentDocumentEditActivity activity;

    @Inject
    @UiField
    Lang lang;

    @UiField
    Element created;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    ValidableTextBox name;
    @UiField
    TextBox fileName;
    @UiField
    Button selectFileButton;
    @UiField
    HTMLPanel documentUploaderContainer;
    @Inject
    @UiField(provided = true)
    DocumentUploader documentUploader;
    @UiField
    OptionItem approved;
    @Inject
    @UiField(provided = true)
    DocumentCategorySelector documentCategory;
    @Inject
    @UiField(provided = true)
    DocumentTypeSelector documentType;
    @UiField
    TextBox version;
    @UiField
    TextBox decimalNumber;
    @UiField
    LongBox inventoryNumber;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector registrar;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector contractor;
    @UiField
    TextArea annotation;
    @Inject
    @UiField(provided = true)
    StringSelectInput keywords;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    private static EquipmentDocumentEditViewUiBinder ourUiBinder = GWT.create(EquipmentDocumentEditViewUiBinder.class);
    interface EquipmentDocumentEditViewUiBinder extends UiBinder<HTMLPanel, EquipmentDocumentEditView> {}
}
