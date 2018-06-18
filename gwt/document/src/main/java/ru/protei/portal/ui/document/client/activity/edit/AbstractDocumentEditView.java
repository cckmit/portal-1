package ru.protei.portal.ui.document.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.document.client.widget.uploader.AbstractDocumentUploader;

import java.util.List;

public interface AbstractDocumentEditView extends IsWidget {

    void setActivity(AbstractDocumentEditActivity activity);

    void setEnabledProject(boolean isEnabled);

    void setVisibleUploader(boolean isVisible);

    void resetFilename();

    HasValue<String> name();

    HasValue<DocumentType> documentType();

    HasValue<ProjectInfo> project();

    HasValue<PersonShortView> manager();

    HasValue<En_DocumentCategory> documentCategory();

    HasValue<String> annotation();

    HasValue<String> created();

    HasValue<String> typeCode();

    HasValue<Long> inventoryNumber();

    HasValue<List<String>> keywords();

    HasValue<DecimalNumber> decimalNumber();

    AbstractDocumentUploader documentUploader();

    HasValidable nameValidator();

    HasValidable decimalNumberValidator();

    void setDecimalNumberExists(boolean isExists);

    void setSaveEnabled(boolean isEnabled);
}
