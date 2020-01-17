package ru.protei.portal.ui.document.client.view.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.document.doccategory.DocumentCategoryFormSelector;
import ru.protei.portal.ui.common.client.widget.document.doctype.DocumentTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;
import ru.protei.portal.ui.common.client.widget.document.uploader.DocumentUploader;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.decimalnumber.DecimalNumberInput;
import ru.protei.portal.ui.common.client.widget.selector.equipment.EquipmentFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectFormSelector;
import ru.protei.portal.ui.common.client.widget.stringselectform.StringTagInputForm;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.document.client.activity.form.AbstractDocumentFormActivity;
import ru.protei.portal.ui.document.client.activity.form.AbstractDocumentFormView;
import ru.protei.portal.ui.document.client.widget.executiontype.DocumentExecutionTypeFormSelector;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class DocumentFormView extends Composite implements AbstractDocumentFormView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        equipment.setVisibleTypes(new HashSet<>(Arrays.asList(En_EquipmentType.values())));
    }

    @Override
    public void setActivity(AbstractDocumentFormActivity activity) {
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
    public void equipmentEnabled(boolean isEnabled) {
        equipment.setEnabled(isEnabled);
    }

    @Override
    public void documentTypeEnabled(boolean isEnabled) {
        documentType.setEnabled(isEnabled);
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
    public void setEquipmentProjectId(Long id) {
        equipment.setProjectId(id);
    }

    @Override
    public void setDocumentTypeCategoryFilter(Selector.SelectorFilter<DocumentType> filter) {
        documentType.setFilter(filter);
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

    private void ensureDebugIds() {}

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

    @Inject
    @UiField
    Lang lang;

    private AbstractDocumentFormActivity activity;

    interface DocumentFormViewUiBinder extends UiBinder<HTMLPanel, DocumentFormView> {}
    private static DocumentFormViewUiBinder ourUiBinder = GWT.create(DocumentFormViewUiBinder.class);
}
