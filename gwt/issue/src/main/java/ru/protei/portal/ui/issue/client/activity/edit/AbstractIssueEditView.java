package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget {

    void setActivity( AbstractIssueEditActivity activity );

    HasValue<String> name();
    HasText description();
    HasValue<En_CaseState> state();
    HasValue<En_ImportanceLevel> importance();
    HasValue<EntityOption> company();
    HasValue<PersonShortView> initiator();
    HasValue<PersonShortView> manager();
    HasValue<ProductShortView> product();
    HasValue<Boolean> isLocal();

    HasValidable nameValidator();
    HasValidable stateValidator();
    HasValidable importanceValidator();
    HasValidable companyValidator();
    HasValidable initiatorValidator();
    HasValidable productValidator();
    HasValidable managerValidator();

    HasEnabled initiatorState();

    void setSubscriptionEmails( String value );

    HasWidgets getCommentsContainer();
    HasAttachments attachmentsContainer();
    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
    void setCaseId(Long caseId);

    void showComments(boolean isShow);
    boolean isAttached();


    HasVisibility saveVisibility();

    HasEnabled companyEnabled();
    HasEnabled productEnabled();
    HasEnabled managerEnabled();
    HasVisibility privacyVisibility();
}
