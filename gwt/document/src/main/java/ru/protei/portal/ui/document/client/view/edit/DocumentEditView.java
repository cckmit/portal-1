package ru.protei.portal.ui.document.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_DocumentExecutionType;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.document.doccategory.DocumentCategoryFormSelector;
import ru.protei.portal.ui.common.client.widget.document.doctype.DocumentTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;
import ru.protei.portal.ui.common.client.widget.document.uploader.DocumentUploader;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.decimalnumber.DecimalNumberInput;
import ru.protei.portal.ui.common.client.widget.selector.equipment.EquipmentFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectFormSelector;
import ru.protei.portal.ui.common.client.widget.stringselectform.StringTagInputForm;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditActivity;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditView;
import ru.protei.portal.ui.document.client.widget.executiontype.DocumentExecutionTypeFormSelector;

import java.util.*;

public class DocumentEditView extends Composite implements AbstractDocumentEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        equipment.setVisibleTypes(new HashSet<>(Arrays.asList(En_EquipmentType.values())));
    }

    @Override
    public void setActivity(AbstractDocumentEditActivity activity) {
        this.activity = activity;
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
    public HasValue<EntityOption> project() {
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
    public AbstractDocumentUploader documentDocUploader() {
        return documentDocUploader;
    }

    @Override
    public AbstractDocumentUploader documentPdfUploader() {
        return documentPdfUploader;
    }

    @Override
    public AbstractDocumentUploader documentApprovalSheetUploader() {
        return documentApprovedUploader;
    }

    @Override
    public HasValue<Boolean> isApproved() {
        return approved;
    }

    @Override
    public HasValue<Set<PersonShortView>> members() {
        return members;
    }

    @Override
    public HasValue<PersonShortView> approvedBy() {
        return approvedBy;
    }

    @Override
    public HasValue<Date> approvalDate() {
        return approvalDate;
    }

    @Override
    public void uploaderEnabled(boolean isEnabled) {
        documentDocUploader.setEnabled(isEnabled);
        documentPdfUploader.setEnabled(isEnabled);
    }

    @Override
    public void projectEnabled(boolean isEnabled) {
        project.setEnabled(isEnabled);
    }

    @Override
    public void executionTypeEnabled(boolean isEnabled) {
        executionType.setEnabled(isEnabled);
    }

    @Override
    public void membersEnabled(boolean isEnabled) {
        members.setEnabled(isEnabled);
    }

    @Override
    public void equipmentEnabled(boolean isEnabled) {
        equipment.setEnabled(isEnabled);
    }

    @Override
    public void documentTypeEnabled(boolean isEnabled) {
        documentType.setEnabled(isEnabled);
    }

    @Override
    public void drawInWizardContainer (boolean isPartOfWizardWidget) {
        if (isPartOfWizardWidget) {
            footer.addClassName("hide");
            card.setStyleName("");
            cardBody.setStyleName("row");
        }
        else {
            footer.removeClassName("hide");
            card.setStyleName("card card-transparent no-margin card-with-fixable-footer");
            cardBody.setStyleName("card-body row no-margin");
        }
    }

    @Override
    public void inventoryNumberEnabled(boolean isEnabled) {
        inventoryNumber.setEnabled(isEnabled);
        inventoryNumberContainer.removeClassName("disabled");
        if (!isEnabled) inventoryNumberContainer.addClassName("disabled");
    }

    @Override
    public void decimalNumberEnabled(boolean isEnabled) {
        decimalNumber.setEnabled(isEnabled);
        decimalNumberContainer.removeClassName("disabled");
        if (!isEnabled) decimalNumberContainer.addClassName("disabled");
    }

    @Override
    public void approvedByEnabled(boolean isEnabled) {
        approvedBy.setEnabled(isEnabled);
        approvedByContainer.removeClassName("disabled");
        if (!isEnabled) approvedByContainer.addClassName("disabled");
    }

    @Override
    public void approvalDateEnabled(boolean isEnabled) {
        approvalDate.setEnabled(isEnabled);
        approvalDateContainer.removeClassName("disabled");
        if (!isEnabled) approvalDateContainer.addClassName("disabled");
    }

    @Override
    public void uploaderApprovalSheetEnabled(boolean isEnabled) {
        documentApprovedUploader.setEnabled(isEnabled);
    }

    @Override
    public void setDecimalNumberHints(List<DecimalNumber> decimalNumberHints) {
        decimalNumber.setHints(decimalNumberHints);
    }

    @Override
    public void setEquipmentProjectIds(Set<Long> ids) {
        equipment.setProjectIds(ids);
    }

    @Override
    public void setDocumentTypeCategoryFilter(Selector.SelectorFilter<DocumentType> filter) {
        documentType.setFilter(filter);
        documentType.refreshValue();
    }

    @Override
    public void setDocumentCategoryValue(List<En_DocumentCategory> documentCategories) {
        documentCategory.fillOptions(documentCategories);
        documentType.refreshValue();
    }

    @Override
    public void setProjectInfo(String customerType, String productDirection, String region) {
        projectCustomerType.setValue(customerType);
        projectProductDirection.setValue(productDirection);
        projectRegion.setValue(region);
    }

    @UiHandler("equipment")
    public void onEquipmentChanged(ValueChangeEvent<EquipmentShortView> event) {
        if (activity != null)
            activity.onEquipmentChanged();
    }

    @UiHandler("documentCategory")
    public void onDocumentCategoryChanged(ValueChangeEvent<En_DocumentCategory> event) {
        if (activity != null)
            activity.onDocumentCategoryChanged();
    }

    @UiHandler("project")
    public void onProjectChanged(ValueChangeEvent<EntityOption> event) {
        setProjectInfo("", "", "");
        if (activity != null)
            activity.onProjectChanged();
    }

    @UiHandler("downloadDoc")
    public void downloadDocClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onDownloadDoc();
        }
    }

    @UiHandler("downloadPdf")
    public void downloadPdfClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onDownloadPdf();
        }
    }

    @UiHandler("approved")
    public void onApprovedChanged(ValueChangeEvent<Boolean> event) {
        if (activity != null)
            activity.onApprovedChanged();
    }

    @UiHandler("cancelBtn")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiHandler("saveBtn")
    public void onSaveClicked (ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    private void ensureDebugIds() {
        name.ensureDebugId(DebugIds.DOCUMENT.CREATE.NAME_INPUT);
        version.ensureDebugId(DebugIds.DOCUMENT.CREATE.VERSION_INPUT);
        inventoryNumber.ensureDebugId(DebugIds.DOCUMENT.CREATE.INVENTORY_INPUT);
        decimalNumber.ensureDebugId(DebugIds.DOCUMENT.CREATE.DECIMAL_NUMBER_INPUT);
        annotation.ensureDebugId(DebugIds.DOCUMENT.CREATE.ANNOTATION_INPUT);
        keywords.ensureInputDebugId(DebugIds.DOCUMENT.CREATE.KEY_WORD_INPUT);
        keywords.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.KEY_WORD_LABEL);
        approved.ensureDebugId(DebugIds.DOCUMENT.CREATE.APPROVED_CHECKBOX);
        approvedBy.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.APPROVED_LABEL);
        approvedBy.ensureDebugId(DebugIds.DOCUMENT.CREATE.APPROVED_SELECTOR);
        approvalDate.setEnsureDebugId(DebugIds.DOCUMENT.CREATE.APPROVE_DATE_INPUT);
        approvalDate.getRelative().ensureDebugId(DebugIds.DOCUMENT.CREATE.APPROVE_DATE_BUTTON);
        downloadDoc.ensureDebugId(DebugIds.DOCUMENT.CREATE.DOC_BUTTON);
        documentDocUploader.ensureDebugId(DebugIds.DOCUMENT.CREATE.DOC_DROP_ZONE);
        downloadPdf.ensureDebugId(DebugIds.DOCUMENT.CREATE.PDF_BUTTON);
        documentPdfUploader.ensureDebugId(DebugIds.DOCUMENT.CREATE.PDF_DROP_ZONE);
        downloadApproved.ensureDebugId(DebugIds.DOCUMENT.CREATE.PDF_APPROVED_BUTTON);
        documentApprovedUploader.ensureDebugId(DebugIds.DOCUMENT.CREATE.PDF_APPROVED_DROP_ZONE);
        registrar.setEnsureDebugId(DebugIds.DOCUMENT.CREATE.REGISTRAR_SELECTOR);
        registrar.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.REGISTRAR_LABEL);
        contractor.setEnsureDebugId(DebugIds.DOCUMENT.CREATE.CONTRACTOR_SELECTOR);
        contractor.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.CONTRACTOR_LABEL);
        project.setEnsureDebugId(DebugIds.DOCUMENT.CREATE.PROJECT_SELECTOR);
        project.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.PROJECT_LABEL);
        projectCustomerType.ensureDebugId(DebugIds.DOCUMENT.CREATE.CUSTOMER_TYPE);
        projectProductDirection.ensureDebugId(DebugIds.DOCUMENT.CREATE.DIRECTION);
        projectRegion.ensureDebugId(DebugIds.DOCUMENT.CREATE.REGION);
        equipment.setEnsureDebugId(DebugIds.DOCUMENT.CREATE.EQUIPMENT_SELECTOR);
        equipment.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.EQUIPMENT_LABEL);
        executionType.setEnsureDebugId(DebugIds.DOCUMENT.CREATE.EXECUTION_TYPE_SELECTOR);
        executionType.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.EXECUTION_TYPE_LABEL);
        documentCategory.setEnsureDebugId(DebugIds.DOCUMENT.CREATE.DOCUMENT_CATEGORY_SELECTOR);
        documentCategory.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.DOCUMENT_CATEGORY_LABEL);
        documentType.setEnsureDebugId(DebugIds.DOCUMENT.CREATE.DOCUMENT_TYPE_SELECTOR);
        documentType.ensureLabelDebugId(DebugIds.DOCUMENT.CREATE.DOCUMENT_TYPE_LABEL);
        members.setAddEnsureDebugId(DebugIds.DOCUMENT.CREATE.EMPLOYEE_ADD_BUTTON);
        members.setClearEnsureDebugId(DebugIds.DOCUMENT.CREATE.EMPLOYEE_CLEAR_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.NAME_LABEL);
        versionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.VERSION_LABEL);
        inventoryNumberLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.INVENTORY_LABEL);
        decimalNumberLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.DECIMAL_NUMBER_LABEL);
        annotationLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.ANNOTATION_LABEL);
        approvalDateLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.APPROVE_DATE_LABEL);
        downloadDocLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.DOC_LABEL);
        downloadPdfLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.PDF_LABEL);
        downloadApprovedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.PDF_APPROVED_LABEL);
        documentSectionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.PROJECT_INFO_LABEL);
        projectCustomerTypeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.CUSTOMER_TYPE_LABEL);
        projectProductDirectionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.DIRECTION_LABEL);
        projectRegionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.REGION_LABEL);
        documentSectionEquipmentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.EQUIPMENT_INFO_LABEL);
        documentSectionInfoLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.DOCUMENT_INFO_LABEL);
        documentMembersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.CREATE.EMPLOYEE_PERMISSION_LABEL);
    }

    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    DocumentUploader documentDocUploader;
    @Inject
    @UiField(provided = true)
    DocumentUploader documentPdfUploader;
    @Inject
    @UiField(provided = true)
    DocumentUploader documentApprovedUploader;
    @Inject
    @UiField(provided = true)
    DocumentTypeFormSelector documentType;
    @Inject
    @UiField(provided = true)
    DocumentCategoryFormSelector documentCategory;
    @UiField
    TextArea annotation;
    @UiField
    LongBox inventoryNumber;
    @UiField
    DivElement inventoryNumberContainer;
    @Inject
    @UiField(provided = true)
    ProjectFormSelector project;
    @UiField
    TextBox projectCustomerType;
    @UiField
    TextBox projectProductDirection;
    @UiField
    TextBox projectRegion;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector contractor;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector registrar;
    @UiField
    TextBox version;
    @Inject
    @UiField(provided = true)
    DocumentExecutionTypeFormSelector executionType;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector members;
    @Inject
    @UiField(provided = true)
    StringTagInputForm keywords;
    @Inject
    @UiField(provided = true)
    DecimalNumberInput decimalNumber;
    @UiField
    DivElement decimalNumberContainer;
    @UiField
    CheckBox approved;
    @UiField
    DivElement approvedByContainer;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector approvedBy;
    @UiField
    DivElement approvalDateContainer;
    @Inject
    @UiField(provided = true)
    SinglePicker approvalDate;
    @Inject
    @UiField(provided = true)
    EquipmentFormSelector equipment;
    @UiField
    Anchor downloadDoc;
    @UiField
    Anchor downloadPdf;
    @UiField
    Anchor downloadApproved;
    @UiField
    LabelElement nameLabel;
    @UiField
    LabelElement versionLabel;
    @UiField
    LabelElement inventoryNumberLabel;
    @UiField
    LabelElement decimalNumberLabel;
    @UiField
    LabelElement annotationLabel;
    @UiField
    LabelElement approvalDateLabel;
    @UiField
    DivElement downloadDocLabel;
    @UiField
    DivElement downloadPdfLabel;
    @UiField
    DivElement downloadApprovedLabel;
    @UiField
    LabelElement documentSectionLabel;
    @UiField
    LabelElement projectCustomerTypeLabel;
    @UiField
    LabelElement projectProductDirectionLabel;
    @UiField
    LabelElement projectRegionLabel;
    @UiField
    LabelElement documentSectionEquipmentLabel;
    @UiField
    LabelElement documentSectionInfoLabel;
    @UiField
    LabelElement documentMembersLabel;
    @UiField
    DivElement footer;
    @UiField
    Button cancelBtn;
    @UiField
    Button saveBtn;
    @UiField
    HTMLPanel card;
    @UiField
    HTMLPanel cardBody;

    @Inject
    @UiField
    Lang lang;
    private AbstractDocumentEditActivity activity;

    interface DocumentEditViewUiBinder extends UiBinder<HTMLPanel, DocumentEditView> {}
    private static DocumentEditViewUiBinder ourUiBinder = GWT.create(DocumentEditViewUiBinder.class);
}
