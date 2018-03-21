package ru.protei.portal.ui.document.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;

import java.util.List;

public interface AbstractDocumentEditView extends IsWidget {

    void setActivity(AbstractDocumentEditActivity activity);

    HasValue<String> name();

    HasValue<DocumentType> documentType();

    HasValue<String> project();

    HasValue<PersonShortView> manager();

    HasValue<String> annotation();

    HasValue<String> created();

    HasValue<Long> inventoryNumber();

    HasValue<List<String>> keywords();

    HasValue<DecimalNumber> decimalNumber();

    boolean isDecimalNumbersCorrect();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    HasAttachments attachmentsContainer();

    void setCaseId(Long id);

    boolean isAttached();
}
