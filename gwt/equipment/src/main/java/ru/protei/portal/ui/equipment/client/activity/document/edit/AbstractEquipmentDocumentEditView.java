package ru.protei.portal.ui.equipment.client.activity.document.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;

import java.util.List;

public interface AbstractEquipmentDocumentEditView extends IsWidget {

    void setActivity(AbstractEquipmentDocumentEditActivity activity);

    void setCreated(String date);

    HasValue<String> name();

    AbstractDocumentUploader documentUploader();

    HasValue<Boolean> approved();

    void setDocumentCategory(En_DocumentCategory documentCategory);

    HasValue<DocumentType> documentType();

    HasValue<String> version();

    HasValue<String> decimalNumber();

    HasValue<Long> inventoryNumber();

    HasValue<PersonShortView> registrar();

    HasValue<PersonShortView> contractor();

    HasValue<String> annotation();

    HasValue<List<String>> keywords();

    void setApprovedMode(boolean off);

    void setDocumentUploaderLabel(String value);

    void setDecimalNumbersAvailableValues(List<String> decimalNumbers);

    HasEnabled documentCategoryEnabled();

    HasEnabled documentTypeEnabled();

    HasEnabled approvedEnabled();

    HasEnabled inventoryNumberEnabled();

    HasEnabled saveButtonEnabled();

    HasEnabled cancelButtonEnabled();

    HasEnabled decimalNumberEnabled();
}
