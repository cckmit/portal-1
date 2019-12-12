package ru.protei.portal.ui.document.client.activity.form;

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

import java.util.List;

public interface AbstractDocumentFormView extends IsWidget {

    void setActivity(AbstractDocumentFormActivity activity);

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

    HasText decimalNumberText();

    HasValidable nameValidator();


    void decimalNumberEnabled(boolean isEnabled);

    void equipmentEnabled(boolean isEnabled);

    void documentTypeEnabled(boolean isEnabled);

    void inventoryNumberEnabled(boolean isEnabled);

    void uploaderEnabled(boolean isEnabled);


    AbstractDocumentUploader documentUploader();


    void setDecimalNumberHints(List<DecimalNumber> decimalNumberHints);

    void setEquipmentProjectId(Long id);

    void setDocumentTypeCategoryFilter(Selector.SelectorFilter<DocumentType> filter);

    void setProjectInfo(String customerType, String productDirection, String region);
}
