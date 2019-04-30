package ru.protei.portal.ui.document.client.view.edit;

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
import ru.protei.portal.core.model.dict.En_DocumentExecutionType;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.document.doccategory.DocumentCategorySelector;
import ru.protei.portal.ui.common.client.widget.document.doctype.DocumentTypeSelector;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;
import ru.protei.portal.ui.common.client.widget.document.uploader.DocumentUploader;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.decimalnumber.DecimalNumberInput;
import ru.protei.portal.ui.common.client.widget.selector.equipment.EquipmentSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectButtonSelector;
import ru.protei.portal.ui.common.client.widget.stringselect.input.StringSelectInput;
import ru.protei.portal.ui.common.client.widget.switcher.Switcher;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditActivity;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditView;
import ru.protei.portal.ui.document.client.widget.executiontype.DocumentExecutionTypeSelector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DocumentEditView extends Composite implements AbstractDocumentEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        fileName.getElement().setAttribute("placeholder", lang.documentUploadPlaceholder());
        equipment.setVisibleTypes(new HashSet<>(Arrays.asList(En_EquipmentType.values())));
    }

    @Override
    public void setActivity(AbstractDocumentEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilename() {
        fileName.setText(null);
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
    public HasValue<En_DocumentExecutionType> executionType() {
        return executionType;
    }

    @Override
    public HasValue<DocumentType> documentType() {
        return documentType;
    }

    @Override
    public HasValue<En_DocumentCategory> documentCategory() {
        return documentCategory;
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
    public HasValue<PersonShortView> contractor() {
        return contractor;
    }

    @Override
    public HasValue<PersonShortView> registrar() {
        return registrar;
    }

    @Override
    public HasValue<EquipmentShortView> equipment() {
        return equipment;
    }

    @Override
    public void setCreated(String date) {
        created.setInnerText(date);
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
    public HasText decimalNumberText() {
        return decimalNumber;
    }

    @Override
    public HasValue<DecimalNumber> decimalNumber() {
        return decimalNumber;
    }

    @Override
    public HasValue<String> version() {
        return version;
    }

    @Override
    public AbstractDocumentUploader documentUploader() {
        return documentUploader;
    }

    @Override
    public HasEnabled decimalNumberEnabled() {
        return decimalNumber;
    }

    @Override
    public HasValue<Boolean> isApproved() { return approved; }

    @Override
    public HasVisibility equipmentVisible() {
        return equipmentSelectorContainer;
    }

    @Override
    public HasVisibility decimalNumberVisible() {
        return decimalNumberContainer;
    }

    @Override
    public HasVisibility inventoryNumberVisible() {
        return inventoryNumberContainer;
    }

    @Override
    public HasVisibility uploaderVisible() {
        return new HasVisibility() {
            @Override
            public boolean isVisible() {
                return documentUploader.isVisible();
            }

            @Override
            public void setVisible(boolean visible) {
                selectFileContainer.setVisible(visible);
                nameContainer.getElement().setClassName("form-group " + (visible ? "col-md-6" : "col-md-9"));
            }
        };
    }

    @Override
    public HasEnabled equipmentEnabled() {
        return equipment;
    }

    @Override
    public HasEnabled documentTypeEnabled() {
        return documentType;
    }

    @Override
    public HasEnabled inventoryNumberEnabled() {
        return inventoryNumber;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public void setDecimalNumberHints(List<DecimalNumber> decimalNumberHints) {
        decimalNumber.setHints(decimalNumberHints);
    }

    @Override
    public void setEquipmentProjectId(Long id) {
        equipment.setProjectId(id);
    }

    @Override
    public void setDocumentTypeCategoryFilter(Selector.SelectorFilter<DocumentType> filter) {
        documentType.setFilter(filter);
        documentType.refreshValue();
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

    @UiHandler("equipment")
    public void onEquipmentChanged(ValueChangeEvent<EquipmentShortView> event) {
        if (activity != null)
            activity.onEquipmentChanged();
    }

    @UiHandler("decimalNumber")
    public void onDecimalNumberChanged(ValueChangeEvent<DecimalNumber> event) {
        if (activity != null)
            activity.onDecimalNumberChanged();
    }

    @UiHandler("documentCategory")
    public void onDocumentCategoryChanged(ValueChangeEvent<En_DocumentCategory> event) {
        if (activity != null)
            activity.onDocumentCategoryChanged();
    }

    @UiHandler("project")
    public void onProjectChanged(ValueChangeEvent<ProjectInfo> event) {
        if (activity != null)
            activity.onProjectChanged();
    }

    @UiHandler("documentUploader")
    public void onFilenameChanged(ChangeEvent event) {
        fileName.setValue(documentUploader.getFilename());
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
    EmployeeButtonSelector contractor;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector registrar;

    @UiField
    Element created;

    @UiField
    TextBox version;

    @Inject
    @UiField(provided = true)
    DocumentExecutionTypeSelector executionType;

    @Inject
    @UiField(provided = true)
    StringSelectInput keywords;

    @Inject
    @UiField(provided = true)
    DecimalNumberInput decimalNumber;

    @UiField
    Switcher approved;

    @UiField
    Button selectFileButton;

    @UiField
    HTMLPanel selectFileContainer;

    @Inject
    @UiField(provided = true)
    EquipmentSelector equipment;

    @UiField
    HTMLPanel equipmentSelectorContainer;
    @UiField
    HTMLPanel decimalNumberContainer;
    @UiField
    HTMLPanel inventoryNumberContainer;
    @UiField
    HTMLPanel approvedContainer;

    @Inject
    @UiField
    Lang lang;

    private AbstractDocumentEditActivity activity;

    private static DocumentViewUiBinder ourUiBinder = GWT.create(DocumentViewUiBinder.class);

    interface DocumentViewUiBinder extends UiBinder<HTMLPanel, DocumentEditView> {
    }
}
