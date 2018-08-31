package ru.protei.portal.ui.document.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;

import java.util.List;

public interface AbstractDocumentEditView extends IsWidget {

    void setActivity(AbstractDocumentEditActivity activity);

    HasValue<String> name();

    HasValue<DocumentType> documentType();

    HasValue<ProjectInfo> project();

    HasValue<PersonShortView> contractor();

    HasValue<PersonShortView> registrar();

    HasValue<En_DocumentCategory> documentCategory();

    HasValue<String> annotation();

    HasValue<EquipmentShortView> equipment();

    void setCreated(String date);

    HasValue<Long> inventoryNumber();

    HasValue<List<String>> keywords();

    HasValue<String> version();

    HasValue<DecimalNumber> decimalNumber();

    HasText decimalNumberText();


    HasValidable nameValidator();


    HasEnabled decimalNumberEnabled();

    HasEnabled equipmentEnabled();

    HasEnabled documentTypeEnabled();


    HasVisibility uploaderVisible();

    HasVisibility equipmentVisible();

    HasVisibility decimalNumberVisible();

    HasVisibility inventoryNumberVisible();


    AbstractDocumentUploader documentUploader();

    void resetFilename();

    void setSaveEnabled(boolean isEnabled);

    void setDecimalNumberHints(List<DecimalNumber> decimalNumberHints);

    void setEquipmentProjectId(Long id);

    void setDocumentTypeCategoryFilter(En_DocumentCategory value);
}
