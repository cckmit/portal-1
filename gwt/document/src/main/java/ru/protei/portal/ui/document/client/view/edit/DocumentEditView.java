package ru.protei.portal.ui.document.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.decimalnumber.DecimalNumberSelector;
import ru.protei.portal.ui.common.client.widget.selector.equipment.EquipmentSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectButtonSelector;
import ru.protei.portal.ui.common.client.widget.stringselect.input.StringSelectInput;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditActivity;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditView;
import ru.protei.portal.ui.document.client.widget.doccategory.DocumentCategorySelector;
import ru.protei.portal.ui.document.client.widget.doctype.DocumentTypeSelector;
import ru.protei.portal.ui.document.client.widget.uploader.AbstractDocumentUploader;
import ru.protei.portal.ui.document.client.widget.uploader.DocumentUploader;

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
    public void setSaveEnabled(boolean isEnabled) {
        this.saveButton.setEnabled(isEnabled);
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
    public HasValue<String> decimalNumber() {
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
    public HasVisibility equipmentVisible() {
        return equipmentSelectorContainer;
    }

    @Override
    public HasEnabled projectEnabled() {
        return project;
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
                documentUploader.setVisible(visible);
                nameContainer.getElement().setClassName("form-group " + (visible ? "col-xs-6" : "col-xs-9"));
            }
        };
    }

    @Override
    public HasEnabled equipmentEnabled() {
        return new HasEnabled() {
            @Override
            public boolean isEnabled() {
                return equipmentDecimalNumber.isEnabled() && equipment.isEnabled();
            }

            @Override
            public void setEnabled(boolean enabled) {
                equipmentDecimalNumber.setEnabled(enabled);
                equipment.setEnabled(enabled);
            }
        };
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
        equipmentDecimalNumber.setValue(null, true);
        if (event.getValue() == null || event.getValue().getDecimalNumbers()== null) {
            equipmentDecimalNumber.setEnabled(false);
        } else {
            List<DecimalNumber> decimalNumbers = event.getValue().getDecimalNumbers();
            equipmentDecimalNumber.fillOptions(decimalNumbers);
            equipmentDecimalNumber.setEnabled(true);

            if (!decimalNumbers.isEmpty())
                equipmentDecimalNumber.setValue(decimalNumbers.get(0), true);
        }

        if (activity != null)
            activity.onEquipmentChanged();
    }

    @UiHandler("equipmentDecimalNumber")
    public void onEquipmentDecimalNumberChanged(ValueChangeEvent<DecimalNumber> event) {
        if (event.getValue() == null)
            decimalNumber.setValue("");
        else
            decimalNumber.setValue(DecimalNumberFormatter.formatNumber(event.getValue()));
    }

    @UiHandler("decimalNumber")
    public void onDecimalNumberChanged(ValueChangeEvent<String> event) {
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
        ProjectInfo value = event.getValue();
        if (value == null) {
            equipmentEnabled().setEnabled(false);
            equipment.setValue(null, true);
        } else {
            equipment.setValue(null, true);
            equipmentEnabled().setEnabled(true);
            equipment.setProjectId(value.getId());
        }
        if (activity != null)
            activity.onProjectChanged();
    }

    @UiHandler("documentCategory")
    public void onCategoryChanged(ValueChangeEvent<En_DocumentCategory> event) {
        documentType.setEnabled(true);
        documentType.setCategoryFilter(event.getValue());
        if (documentType.getValue() != null && !documentType.getValue().getDocumentCategory().equals(event.getValue())) {
            documentType.setValue(null);
        }
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
    TextBox created;

    @UiField
    TextBox version;

    @Inject
    @UiField(provided = true)
    StringSelectInput keywords;

    @UiField
    TextBox decimalNumber;

    @UiField
    Button selectFileButton;

    @UiField
    HTMLPanel selectFileContainer;

    @Inject
    @UiField(provided = true)
    EquipmentSelector equipment;

    @Inject
    @UiField(provided = true)
    DecimalNumberSelector equipmentDecimalNumber;

    @UiField
    HTMLPanel equipmentSelectorContainer;

    @Inject
    Lang lang;

    AbstractDocumentEditActivity activity;

    private static DocumentViewUiBinder ourUiBinder = GWT.create(DocumentViewUiBinder.class);

    interface DocumentViewUiBinder extends UiBinder<HTMLPanel, DocumentEditView> {
    }
}
