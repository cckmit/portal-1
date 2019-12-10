package ru.protei.portal.ui.issue.client.activity.create;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

public interface AbstractIssueCreateView extends IsWidget {
    void setActivity(AbstractIssueCreateActivity issueCreateActivity);

    HasValue<String> name();

    HasValue<String> description();

    HasValue<Boolean> isPrivate();

    HasValue<Set<CaseLink>> links();

    HasValue<Set<CaseTag>> tags();

    HasValidable nameValidator();

    HasAttachments attachmentsContainer();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    HasVisibility saveVisibility();

    HasVisibility privacyVisibility();

    HasEnabled saveEnabled();

    void setTagsAddButtonEnabled(boolean enabled);

    void setTagsEditButtonEnabled(boolean enabled);

    void setDescriptionPreviewAllowed( boolean isPreviewAllowed );

    String DESCRIPTION = "description";

    HasWidgets getIssueMetaViewContainer();
}
