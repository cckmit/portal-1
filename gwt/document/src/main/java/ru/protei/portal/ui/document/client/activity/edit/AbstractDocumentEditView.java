package ru.protei.portal.ui.document.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_DocumentExecutionType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AbstractDocumentEditView extends IsWidget {

    void setActivity(AbstractDocumentEditActivity activity);

    HasValue<String> name();

    HasValue<En_DocumentExecutionType> executionType();

    HasValue<DocumentType> documentType();

    HasValue<EntityOption> project();

    HasValue<PersonShortView> contractor();

    HasValue<PersonShortView> registrar();

    HasValue<En_DocumentCategory> documentCategory();

    HasValue<String> annotation();

    HasValue<EquipmentShortView> equipment();

    HasValue<Long> inventoryNumber();

    HasValue<List<String>> keywords();

    HasValue<String> version();

    HasValue<DecimalNumber> decimalNumber();

    HasValue<Boolean> isApproved();

    HasValue<PersonShortView> approvedBy();

    HasValue<Date> approvalDate();

    HasValue<Set<PersonShortView>> members();

    HasText decimalNumberText();

    HasValidable nameValidator();


    void decimalNumberEnabled(boolean isEnabled);

    void equipmentEnabled(boolean isEnabled);

    void documentTypeEnabled(boolean isEnabled);

    void drawInWizardContainer (boolean isPartOfWizardWidget);

    void inventoryNumberEnabled(boolean isEnabled);

    void uploaderEnabled(boolean isEnabled);

    void projectEnabled(boolean isEnabled);

    void membersEnabled(boolean isEnabled);

    void executionTypeEnabled(boolean isEnabled);

    void approvedByEnabled(boolean isEnabled);

    void approvalDateEnabled(boolean isEnabled);

    void inventoryNumberMandatory(boolean isMandatory);

    void uploaderApprovalSheetEnabled(boolean isEnabled);

    AbstractDocumentUploader documentDocUploader();

    AbstractDocumentUploader documentPdfUploader();

    AbstractDocumentUploader documentApprovalSheetUploader();

    void setDecimalNumberHints(List<DecimalNumber> decimalNumberHints);

    void setEquipmentProjectIds(Set<Long> ids);

    void setDocumentTypeCategoryFilter(Selector.SelectorFilter<DocumentType> filter);

    void setDocumentCategoryValue(List<En_DocumentCategory> documentCategories);

    void setProjectInfo(String customerType, String productDirection, String region);

    void setApprovalFieldsMandatory (boolean isMandatory);

    void setButtonsEnabled (boolean isEnabled);

    void setDownloadCloudsVisible (boolean isVisible);
}
