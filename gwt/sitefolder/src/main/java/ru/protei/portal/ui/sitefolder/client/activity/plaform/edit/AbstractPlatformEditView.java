package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractPlatformEditView extends IsWidget {

    void setActivity(AbstractPlatformEditActivity activity);

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    void setCaseNumber(Long caseNumber);

    HasValue<String> name();

    HasValue<EntityOption> company();

    HasValue<PersonShortView> manager();

    HasEnabled managerEnabled();

    HasValue<String> parameters();

    HasValue<String> comment();

    Panel serversContainer();

    HasVisibility serversContainerVisibility();

    HasEnabled companyEnabled();

    HasValidable nameValidator();

    HasValidable companyValidator();

    HasWidgets contactsContainer();

    HasAttachments attachmentsContainer();

    HasValue<EntityOption> project();

    String COMMENT_DISPLAY_PREVIEW = "platform_view_is_comment_preview_displayed";

    Element getRootElement();
}
